package com.taotao.cloud.member.biz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.enums.SwitchEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.utils.common.SecurityUtil;
import com.taotao.cloud.common.utils.lang.StringUtil;
import com.taotao.cloud.goods.api.feign.IFeignGoodsSkuService;
import com.taotao.cloud.goods.api.web.vo.GoodsSkuSpecGalleryVO;
import com.taotao.cloud.member.api.web.query.EvaluationPageQuery;
import com.taotao.cloud.member.api.web.dto.MemberEvaluationDTO;
import com.taotao.cloud.member.api.enums.EvaluationGradeEnum;
import com.taotao.cloud.member.api.web.vo.EvaluationNumberVO;
import com.taotao.cloud.member.biz.model.entity.Member;
import com.taotao.cloud.member.biz.model.entity.MemberEvaluation;
import com.taotao.cloud.member.biz.mapper.MemberEvaluationMapper;
import com.taotao.cloud.member.biz.service.MemberEvaluationService;
import com.taotao.cloud.member.biz.service.MemberService;
import com.taotao.cloud.order.api.enums.order.CommentStatusEnum;
import com.taotao.cloud.order.api.feign.IFeignOrderItemService;
import com.taotao.cloud.order.api.feign.IFeignOrderService;
import com.taotao.cloud.order.api.web.vo.order.OrderItemVO;
import com.taotao.cloud.order.api.web.vo.order.OrderVO;
import com.taotao.cloud.stream.framework.rocketmq.RocketmqSendCallbackBuilder;
import com.taotao.cloud.stream.framework.rocketmq.tags.GoodsTagsEnum;
import com.taotao.cloud.stream.properties.RocketmqCustomProperties;
import com.taotao.cloud.web.sensitive.word.SensitiveWordsFilter;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员商品评价业务层实现
 *
 * @since 2020-02-25 14:10:16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberEvaluationServiceImpl extends
	ServiceImpl<MemberEvaluationMapper, MemberEvaluation> implements MemberEvaluationService {

	/**
	 * 会员评价数据层
	 */
	@Resource
	private MemberEvaluationMapper memberEvaluationMapper;
	/**
	 * 订单
	 */
	@Autowired
	private IFeignOrderService orderService;
	/**
	 * 子订单
	 */
	@Autowired
	private IFeignOrderItemService orderItemService;
	/**
	 * 会员
	 */
	@Autowired
	private MemberService memberService;
	/**
	 * 商品
	 */
	@Autowired
	private IFeignGoodsSkuService goodsSkuService;
	/**
	 * rocketMq
	 */
	@Autowired
	private RocketMQTemplate rocketMQTemplate;
	/**
	 * rocketMq配置
	 */
	@Autowired
	private RocketmqCustomProperties rocketmqCustomProperties;

	@Override
	public IPage<MemberEvaluation> managerQuery(EvaluationPageQuery queryParams) {
		//获取评价分页
		return this.page(queryParams.buildMpPage(), queryParams.queryWrapper());
	}

	@Override
	public IPage<MemberEvaluation> queryPage(EvaluationPageQuery evaluationPageQuery) {
		return memberEvaluationMapper.getMemberEvaluationList(
			evaluationPageQuery.buildMpPage(), evaluationPageQuery.queryWrapper());
	}

	@Override
	public Boolean addMemberEvaluation(MemberEvaluationDTO memberEvaluationDTO) {
		//获取子订单信息
		OrderItemVO orderItem = orderItemService.getBySn(memberEvaluationDTO.getOrderItemSn());
		//获取订单信息
		OrderVO order = orderService.getBySn(orderItem.orderSn()).data();
		//检测是否可以添加会员评价
		checkMemberEvaluation(orderItem, order);
		//获取用户信息
		Member member = memberService.getUserInfo();
		//获取商品信息
		GoodsSkuSpecGalleryVO goodsSku = goodsSkuService.getGoodsSkuByIdFromCache(
			memberEvaluationDTO.getSkuId()).data();
		//新增用户评价
		MemberEvaluation memberEvaluation = new MemberEvaluation(memberEvaluationDTO, goodsSku,
			member, order);
		//过滤商品咨询敏感词
		memberEvaluation.setContent(SensitiveWordsFilter.filter(memberEvaluation.getContent()));
		//添加评价
		this.save(memberEvaluation);

		//修改订单货物评价状态为已评价
		orderItemService.updateCommentStatus(orderItem.sn(), CommentStatusEnum.FINISHED);
		//发送商品评价消息
		String destination = rocketmqCustomProperties.getGoodsTopic() + ":"
			+ GoodsTagsEnum.GOODS_COMMENT_COMPLETE.name();
		rocketMQTemplate.asyncSend(destination, JSONUtil.toJsonStr(memberEvaluation),
			RocketmqSendCallbackBuilder.commonCallback());
		return true;
	}

	@Override
	public MemberEvaluation queryById(Long id) {
		return this.getById(id);
	}

	@Override
	public Boolean updateStatus(Long id, String status) {
		UpdateWrapper<MemberEvaluation> updateWrapper = Wrappers.update();
		updateWrapper.eq("id", id);
		updateWrapper.set("status", status.equals(SwitchEnum.OPEN.name()) ? SwitchEnum.OPEN.name()
			: SwitchEnum.CLOSE.name());
		return this.update(updateWrapper);
	}

	@Override
	public Boolean delete(Long id) {
		LambdaUpdateWrapper<MemberEvaluation> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.set(MemberEvaluation::getDelFlag, true);
		updateWrapper.eq(MemberEvaluation::getId, id);
		return this.update(updateWrapper);
	}

	@Override
	public Boolean reply(Long id, String reply, String replyImage) {
		UpdateWrapper<MemberEvaluation> updateWrapper = Wrappers.update();
		updateWrapper.set("reply_status", true);
		updateWrapper.set("reply", reply);
		if (StringUtil.isNotBlank(replyImage)) {
			updateWrapper.set("have_reply_image", true);
			updateWrapper.set("reply_image", replyImage);
		}
		updateWrapper.eq("id", id);
		return this.update(updateWrapper);
	}

	@Override
	public EvaluationNumberVO getEvaluationNumber(Long goodsId) {
		EvaluationNumberVO evaluationNumberVO = new EvaluationNumberVO();
		List<Map<String, Object>> list = this.baseMapper.getEvaluationNumber(goodsId);

		int good = 0;
		int moderate = 0;
		int worse = 0;
		for (Map<String, Object> map : list) {
			if (map.get("grade").equals(EvaluationGradeEnum.GOOD.name())) {
				good = Integer.parseInt(map.get("num").toString());
			} else if (map.get("grade").equals(EvaluationGradeEnum.MODERATE.name())) {
				moderate = Integer.parseInt(map.get("num").toString());
			} else if (map.get("grade").equals(EvaluationGradeEnum.WORSE.name())) {
				worse = Integer.parseInt(map.get("num").toString());
			}
		}
		evaluationNumberVO.setAll(good + moderate + worse);
		evaluationNumberVO.setGood(good);
		evaluationNumberVO.setModerate(moderate);
		evaluationNumberVO.setWorse(worse);
		evaluationNumberVO.setHaveImage(this.count(new QueryWrapper<MemberEvaluation>()
			.eq("have_image", 1)
			.eq("goods_id", goodsId)));

		return evaluationNumberVO;
	}

	/**
	 * 检测会员评价
	 *
	 * @param orderItem 子订单
	 * @param order     订单
	 */
	public void checkMemberEvaluation(OrderItemVO orderItem, OrderVO order) {
		//根据子订单编号判断是否评价过
		if (orderItem.commentStatus().equals(CommentStatusEnum.FINISHED.name())) {
			throw new BusinessException("已评价");
		}

		//判断是否是当前会员的订单
		if (!order.orderBase().memberId().equals(SecurityUtil.getUserId())) {
			throw new BusinessException(ResultEnum.ORDER_NOT_USER);
		}
	}

}
