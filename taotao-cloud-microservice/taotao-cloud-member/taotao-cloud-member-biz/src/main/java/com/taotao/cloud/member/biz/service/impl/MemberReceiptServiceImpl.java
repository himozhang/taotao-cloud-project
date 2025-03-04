package com.taotao.cloud.member.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.utils.bean.BeanUtil;
import com.taotao.cloud.common.utils.lang.StringUtil;
import com.taotao.cloud.member.api.web.query.MemberReceiptPageQuery;
import com.taotao.cloud.member.api.web.vo.MemberReceiptAddVO;
import com.taotao.cloud.member.biz.model.entity.Member;
import com.taotao.cloud.member.biz.model.entity.MemberReceipt;
import com.taotao.cloud.member.biz.mapper.MemberReceiptMapper;
import com.taotao.cloud.member.biz.service.MemberReceiptService;
import com.taotao.cloud.member.biz.service.MemberService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 会员发票业务层实现
 */
@Service
public class MemberReceiptServiceImpl extends ServiceImpl<MemberReceiptMapper, MemberReceipt> implements
	MemberReceiptService {

	@Autowired
	private MemberService memberService;

	@Override
	public IPage<MemberReceipt> getPage(MemberReceiptPageQuery memberReceiptPageQuery) {
		LambdaQueryWrapper<MemberReceipt> queryWrapper = new LambdaQueryWrapper<>();
		//会员名称查询
		if (StringUtil.isNotEmpty(memberReceiptPageQuery.getMemberName())) {
			queryWrapper.like(MemberReceipt::getMemberName, memberReceiptPageQuery.getMemberName());
		}
		//会员id查询
		if (StringUtil.isNotEmpty(memberReceiptPageQuery.getMemberId())) {
			queryWrapper.eq(MemberReceipt::getMemberId, memberReceiptPageQuery.getMemberId());
		}
		//会员id查询
		if (StringUtil.isNotEmpty(memberReceiptPageQuery.getReceiptType())) {
			queryWrapper.eq(MemberReceipt::getReceiptType, memberReceiptPageQuery.getReceiptType());
		}
		queryWrapper.eq(MemberReceipt::getDeleteFlag, true);
		queryWrapper.orderByDesc(MemberReceipt::getCreateTime);
		return this.page(memberReceiptPageQuery.buildMpPage(), queryWrapper);
	}

	@Override
	public Boolean addMemberReceipt(MemberReceiptAddVO memberReceiptAddVO, Long memberId) {
		//校验发票抬头是否重复
		List<MemberReceipt> receipts = this.baseMapper.selectList(new QueryWrapper<MemberReceipt>()
			.eq("member_id", memberId)
			.eq("receipt_title", memberReceiptAddVO.getReceiptTitle())
		);
		if (receipts.size() > 0) {
			throw new BusinessException(ResultEnum.USER_RECEIPT_REPEAT_ERROR);
		}
		//参数封装
		MemberReceipt memberReceipt = new MemberReceipt();
		BeanUtil.copyProperties(memberReceiptAddVO, memberReceipt);
		//根据会员信息查询会员
		Member member = memberService.getById(memberId);
		if (member != null) {
			memberReceipt.setMemberId(memberId);
			memberReceipt.setMemberName(member.getUsername());
			//设置发票默认
			List<MemberReceipt> list = this.baseMapper.selectList(
				new QueryWrapper<MemberReceipt>().eq("member_id", memberId));
			//如果当前会员只有一个发票则默认为默认发票，反之需要校验参数默认值，做一些处理
			if (list.size() <= 0) {
				memberReceipt.setDefaulted(1);
			} else {
				if (memberReceiptAddVO.getIsDefault().equals(1)) {
					//如果参数传递新添加的发票信息为默认，则需要把其他发票置为非默认
					this.update(new UpdateWrapper<MemberReceipt>().eq("member_id", memberId));
					//设置当前发票信息为默认
					memberReceipt.setDefaulted(memberReceiptAddVO.getIsDefault());
				} else {
					memberReceiptAddVO.setIsDefault(0);
				}
			}
			return this.baseMapper.insert(memberReceipt) > 0;
		}
		throw new BusinessException(ResultEnum.USER_RECEIPT_NOT_EXIST);
	}

	@Override
	public Boolean editMemberReceipt(MemberReceiptAddVO memberReceiptAddVO, Long memberId) {
		//根据会员id查询发票信息
		MemberReceipt memberReceiptDb = this.baseMapper.selectById(memberReceiptAddVO.getId());
		if (memberReceiptDb != null) {
			//检验是否有权限修改
			if (!memberReceiptDb.getMemberId().equals(memberId)) {
				throw new BusinessException(ResultEnum.USER_AUTHORITY_ERROR);
			}
			//校验发票抬头是否重复
			List<MemberReceipt> receipts = this.baseMapper.selectList(
				new QueryWrapper<MemberReceipt>()
					.eq("member_id", memberId)
					.eq("receipt_title", memberReceiptAddVO.getReceiptTitle())
					.ne("id", memberReceiptAddVO.getId())
			);
			if (receipts.size() > 0) {
				throw new BusinessException(ResultEnum.USER_RECEIPT_REPEAT_ERROR);
			}
			BeanUtil.copyProperties(memberReceiptAddVO, memberReceiptDb);
			//对发票默认进行处理  如果参数传递新添加的发票信息为默认，则需要把其他发票置为非默认
			if (memberReceiptAddVO.getIsDefault().equals(1)) {
				this.update(new UpdateWrapper<MemberReceipt>().eq("member_id", memberId));
			}
			return this.baseMapper.updateById(memberReceiptDb) > 0;
		}
		throw new BusinessException(ResultEnum.USER_RECEIPT_NOT_EXIST);
	}

	@Override
	public Boolean deleteMemberReceipt(Long id) {
		//根据会员id查询发票信息
		MemberReceipt memberReceiptDb = this.baseMapper.selectById(id);
		if (memberReceiptDb != null) {
			//如果会员发票信息不为空 则逻辑删除此发票信息
			memberReceiptDb.setDeleteFlag(false);
			this.baseMapper.updateById(memberReceiptDb);
		}
		return true;
	}
}
