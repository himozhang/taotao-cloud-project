package com.taotao.cloud.promotion.biz.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import com.taotao.cloud.common.enums.PromotionTypeEnum;
import com.taotao.cloud.common.enums.ResultEnum;
import com.taotao.cloud.common.exception.BusinessException;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.goods.api.feign.IFeignEsGoodsIndexService;
import com.taotao.cloud.promotion.api.enums.PromotionsApplyStatusEnum;
import com.taotao.cloud.promotion.api.tools.PromotionTools;
import com.taotao.cloud.promotion.api.web.vo.SeckillVO;
import com.taotao.cloud.promotion.biz.model.entity.Seckill;
import com.taotao.cloud.promotion.biz.model.entity.SeckillApply;
import com.taotao.cloud.promotion.biz.mapper.SeckillMapper;
import com.taotao.cloud.promotion.biz.service.SeckillApplyService;
import com.taotao.cloud.promotion.biz.service.SeckillService;
import com.taotao.cloud.sys.api.enums.SettingEnum;
import com.taotao.cloud.sys.api.feign.IFeignSettingService;
import com.taotao.cloud.sys.api.web.vo.setting.SettingVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 秒杀活动业务层实现
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-27 16:46:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SeckillServiceImpl extends AbstractPromotionsServiceImpl<SeckillMapper, Seckill> implements
	SeckillService {

    /**
     * 商品索引
     */
    @Autowired
    private IFeignEsGoodsIndexService goodsIndexService;
    /**
     * 设置
     */
    @Autowired
    private IFeignSettingService settingService;

    @Autowired
    private SeckillApplyService seckillApplyService;


    @Override
    public SeckillVO getSeckillDetail(String id) {
        Seckill seckill = this.checkSeckillExist(id);
        SeckillVO seckillVO = new SeckillVO();
        BeanUtils.copyProperties(seckill, seckillVO);
        seckillVO.setSeckillApplyList(this.seckillApplyService.list(new LambdaQueryWrapper<SeckillApply>().eq(SeckillApply::getSeckillId, id)));
        return seckillVO;
    }

    @Override
    public void init() {
        //清除演示数据

        List<Seckill> seckillList = this.list();
        for (Seckill seckill : seckillList) {
            seckill.setStartTime(null);
            seckill.setEndTime(null);
            this.goodsIndexService.updateEsGoodsIndexAllByList(seckill, PromotionTypeEnum.SECKILL.name() + "-" + seckill.getId());
        }
        this.remove(new QueryWrapper<>());

	    Result<SettingVO> settingResult = settingService.get(SettingEnum.SECKILL_SETTING.name());
        SeckillSetting seckillSetting = new Gson().fromJson(settingResult.data().getSettingValue(), SeckillSetting.class);

        for (int i = 1; i <= PRE_CREATION; i++) {
            Seckill seckill = new Seckill(i, seckillSetting.getHours(), seckillSetting.getSeckillRule());
            this.savePromotions(seckill);
        }
    }

    @Override
    public long getApplyNum() {
        DateTime now = DateUtil.date();
        LambdaQueryWrapper<Seckill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(Seckill::getApplyEndTime, now);
        queryWrapper.le(Seckill::getStartTime, now);
        queryWrapper.ge(Seckill::getEndTime, now);
        return this.count(queryWrapper);
    }

    @Override
    public void updateSeckillGoodsNum(String seckillId) {
        Seckill seckill = this.getById(seckillId);
        if (seckill != null) {
            LambdaUpdateWrapper<Seckill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Seckill::getId, seckillId);
            updateWrapper.set(Seckill::getGoodsNum,
                    this.seckillApplyService.count(new LambdaQueryWrapper<SeckillApply>().eq(SeckillApply::getSeckillId, seckillId)));
            this.update(updateWrapper);

        }
    }

    /**
     * 更新商品索引限时抢购信息
     *
     * @param seckill 限时抢购信息
     */
    @Override
    public void updateEsGoodsSeckill(Seckill seckill, List<SeckillApply> seckillApplies) {
        if (seckillApplies != null && !seckillApplies.isEmpty()) {
            //循环秒杀商品数据，将数据按照时间段进行存储
            for (SeckillApply seckillApply : seckillApplies) {
                if (seckillApply.getPromotionApplyStatus().equals(PromotionsApplyStatusEnum.PASS.name())) {
                    this.setSeckillApplyTime(seckill, seckillApply);
                    log.info("更新限时抢购商品状态:{}", seckill);
                    String promotionKey = PromotionTypeEnum.SECKILL.name() + "-" + seckillApply.getTimeLine();
                    this.goodsIndexService.updateEsGoodsIndexPromotions(seckillApply.getSkuId(), seckill, promotionKey, seckillApply.getPrice());
                }
            }
        }
    }

    @Override
    public void setSeckillApplyTime(Seckill seckill, SeckillApply seckillApply) {
        //下一个时间，默认为当天结束时间
        int nextHour = PromotionTools.nextHour(seckill.getHours().split(","), seckillApply.getTimeLine());

        String format = DateUtil.format(seckill.getStartTime(), DatePattern.NORM_DATE_PATTERN);
        DateTime parseStartTime = DateUtil.parse((format + " " + seckillApply.getTimeLine()), "yyyy-MM-dd HH");
        DateTime parseEndTime = DateUtil.parse((format + " " + nextHour), "yyyy-MM-dd HH");
        //如果是当天最后的时间段则设置到当天结束时间的59分59秒
        if (nextHour == seckillApply.getTimeLine()) {
            parseEndTime = DateUtil.parse((format + " " + nextHour + ":59:59"), DatePattern.NORM_DATETIME_PATTERN);
        }
        seckill.setStartTime(parseStartTime);
        //当时商品的秒杀活动活动结束时间为下个时间段的开始
        seckill.setEndTime(parseEndTime);
    }

    /**
     * 检查该秒杀活动是否存在
     *
     * @param id 秒杀活动编号
     * @return 秒杀活动信息
     */
    private Seckill checkSeckillExist(String id) {
        Seckill seckill = this.getById(id);
        if (seckill == null) {
            throw new BusinessException(ResultEnum.SECKILL_NOT_EXIST_ERROR);
        }
        return seckill;
    }

    /**
     * 初始化促销字段
     *
     * @param promotions 促销实体
     */
    @Override
    public void initPromotion(Seckill promotions) {
        super.initPromotion(promotions);
        if (promotions.getStartTime() != null && promotions.getEndTime() == null) {
            promotions.setEndTime(DateUtil.endOfDay(promotions.getStartTime()));
        }
    }

    /**
     * 检查促销状态
     *
     * @param promotions 促销实体
     */
    @Override
    public void checkStatus(Seckill promotions) {
        super.checkStatus(promotions);
        if (promotions.getStartTime() != null && CharSequenceUtil.isNotEmpty(promotions.getHours())) {
            String[] split = promotions.getHours().split(",");
            Arrays.sort(split);
            String startTimeStr = DateUtil.format(promotions.getStartTime(), DatePattern.NORM_DATE_PATTERN) + " " + split[0] + ":00";
            promotions.setStartTime(DateUtil.parse(startTimeStr, DatePattern.NORM_DATETIME_MINUTE_PATTERN));
            promotions.setEndTime(DateUtil.endOfDay(promotions.getStartTime()));
        }
        if (promotions.getStartTime() != null && promotions.getEndTime() != null) {
            //同一时间段内相同的活动
            QueryWrapper<Seckill> queryWrapper = PromotionTools.checkActiveTime(promotions.getStartTime(), promotions.getEndTime(), PromotionTypeEnum.SECKILL, null, promotions.getId());
            long sameNum = this.count(queryWrapper);
            //当前时间段是否存在同类活动
            if (sameNum > 0) {
                throw new BusinessException(ResultEnum.PROMOTION_SAME_ACTIVE_EXIST);
            }
        }
    }

    @Override
    public PromotionTypeEnum getPromotionType() {
        return PromotionTypeEnum.SECKILL;
    }
}
