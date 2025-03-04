package com.taotao.cloud.distribution.biz.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.common.utils.number.CurrencyUtil;
import com.taotao.cloud.distribution.api.enums.DistributionOrderStatusEnum;
import com.taotao.cloud.distribution.api.web.query.DistributionOrderPageQuery;
import com.taotao.cloud.distribution.biz.model.entity.Distribution;
import com.taotao.cloud.distribution.biz.model.entity.DistributionOrder;
import com.taotao.cloud.distribution.biz.mapper.DistributionOrderMapper;
import com.taotao.cloud.distribution.biz.service.DistributionOrderService;
import com.taotao.cloud.distribution.biz.service.DistributionService;
import com.taotao.cloud.order.api.enums.order.PayStatusEnum;
import com.taotao.cloud.order.api.feign.IFeignOrderService;
import com.taotao.cloud.order.api.web.query.order.StoreFlowPageQuery;
import com.taotao.cloud.sys.api.dto.DistributionSetting;
import com.taotao.cloud.sys.api.enums.SettingEnum;
import com.taotao.cloud.sys.api.feign.IFeignSettingService;
import com.taotao.cloud.sys.api.web.vo.setting.SettingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 分销订单接口实现
 */

@Service
public class DistributionOrderServiceImpl extends ServiceImpl<DistributionOrderMapper, DistributionOrder> implements
	DistributionOrderService {

    /**
     * 订单
     */
    @Autowired
    private IFeignOrderService orderService;
    /**
     * 店铺流水
     */
    @Autowired
    private StoreFlowService storeFlowService;
    /**
     * 分销员
     */
    @Autowired
    private DistributionService distributionService;
    /**
     * 系统设置
     */
    @Autowired
    private IFeignSettingService settingService;

    @Override
    public IPage<DistributionOrder> getDistributionOrderPage(
	    DistributionOrderPageQuery distributionOrderPageQuery) {
        return this.page(distributionOrderPageQuery.buildMpPage(), distributionOrderPageQuery.queryWrapper());

    }

    /**
     * 1.查看订单是否为分销订单
     * 2.查看店铺流水计算分销总佣金
     * 3.修改分销员的分销总金额、冻结金额
     *
     * @param orderSn 订单编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculationDistribution(String orderSn) {

        //根据订单编号获取订单数据
        Order order = orderService.getBySn(orderSn);

        //判断是否为分销订单，如果为分销订单则获取分销佣金
        if (order.getDistributionId() != null) {
            //根据订单编号获取有分销金额的店铺流水记录
            List<StoreFlow> storeFlowList = storeFlowService
                    .listStoreFlow(StoreFlowPageQuery.builder().justDistribution(true).orderSn(orderSn).build());
            BigDecimal rebate = 0.0;
            //循环店铺流水记录判断是否包含分销商品
            //包含分销商品则进行记录分销订单、计算分销总额
            for (StoreFlow storeFlow : storeFlowList) {
                rebate = CurrencyUtil.add(rebate, storeFlow.getDistributionRebate());
                DistributionOrder distributionOrder = new DistributionOrder(storeFlow);
                distributionOrder.setDistributionId(order.getDistributionId());
                //分销员信息
                Distribution distribution = distributionService.getById(order.getDistributionId());
                distributionOrder.setDistributionName(distribution.getMemberName());

                //设置结算天数(解冻日期)
	            Result<SettingVO> settingResult = settingService.get(SettingEnum.DISTRIBUTION_SETTING.name());
	            DistributionSetting distributionSetting = JSONUtil.toBean(
		            settingResult.data().getSettingValue(), DistributionSetting.class);
				//默认解冻1天
                if (distributionSetting.getCashDay().equals(0)) {
                    distributionOrder.setSettleCycle(new DateTime());
                } else {
                    DateTime dateTime = new DateTime();
                    dateTime = dateTime.offsetNew(DateField.DAY_OF_MONTH, distributionSetting.getCashDay());
                    distributionOrder.setSettleCycle(dateTime);
                }

                //添加分销订单
                this.save(distributionOrder);

                //记录会员的分销总额
                if (rebate != 0.0) {
                    distributionService.addRebate(rebate, order.getDistributionId());

                    //如果天数写0则立即进行结算
                    if (distributionSetting.getCashDay().equals(0)) {
                        DateTime dateTime = new DateTime();
                        dateTime = dateTime.offsetNew(DateField.MINUTE, 5);
                        //计算分销提佣
                        this.baseMapper.rebate(DistributionOrderStatusEnum.WAIT_BILL.name(), dateTime);

                        //修改分销订单状态
                        this.update(new LambdaUpdateWrapper<DistributionOrder>()
                                .eq(DistributionOrder::getDistributionOrderStatus, DistributionOrderStatusEnum.WAIT_BILL.name())
                                .le(DistributionOrder::getSettleCycle, dateTime)
                                .set(DistributionOrder::getDistributionOrderStatus, DistributionOrderStatusEnum.WAIT_CASH.name()));
                    }
                }
            }
        }
    }

    /**
     * 1.获取订单判断是否为已付款的分销订单
     * 2.查看店铺流水记录分销佣金
     * 3.修改分销员的分销总金额、可提现金额
     *
     * @param orderSn 订单编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderSn) {
        //根据订单编号获取订单数据
        Order order = orderService.getBySn(orderSn);

        //判断是否为已付款的分销订单，则获取分销佣金
        if (order.getDistributionId() != null && order.getPayStatus().equals(PayStatusEnum.PAID.name())) {

            //根据订单编号获取有分销金额的店铺流水记录
            List<DistributionOrder> distributionOrderList = this.list(new LambdaQueryWrapper<DistributionOrder>()
                    .eq(DistributionOrder::getOrderSn, orderSn));

            //如果没有分销定单，则直接返回
            if (distributionOrderList.isEmpty()) {
                return;
            }
            //分销金额
            BigDecimal rebate = 0.0;

            //包含分销商品则进行记录分销订单、计算分销总额
            for (DistributionOrder distributionOrder : distributionOrderList) {
                rebate = CurrencyUtil.add(rebate, distributionOrder.getRebate());
            }

            //如果包含分销商品则记录会员的分销总额
            if (rebate != 0.0) {
                distributionService.subCanRebate(CurrencyUtil.sub(0, rebate), order.getDistributionId());
            }
        }

        //修改分销订单的状态
        this.update(new LambdaUpdateWrapper<DistributionOrder>().eq(DistributionOrder::getOrderSn, orderSn)
                .set(DistributionOrder::getDistributionOrderStatus, DistributionOrderStatusEnum.CANCEL.name()));

    }

    @Override
    public void refundOrder(String afterSaleSn) {
        //判断是否为分销订单
        StoreFlow storeFlow = storeFlowService.queryOne(StoreFlowPageQuery.builder().justDistribution(true).refundSn(afterSaleSn).build());
        if (storeFlow != null) {

            //获取收款分销订单
            DistributionOrder distributionOrder = this.getOne(new LambdaQueryWrapper<DistributionOrder>()
                    .eq(DistributionOrder::getOrderItemSn, storeFlow.getOrderItemSn()));
            //分销订单不存在，则直接返回
            if (distributionOrder == null) {
                return;
            }
            if (distributionOrder.getDistributionOrderStatus().equals(DistributionOrderStatusEnum.WAIT_BILL.name())) {
                this.update(new LambdaUpdateWrapper<DistributionOrder>()
                        .eq(DistributionOrder::getOrderItemSn, storeFlow.getOrderItemSn())
                        .set(DistributionOrder::getDistributionOrderStatus, DistributionOrderStatusEnum.CANCEL.name()));
            }
            //如果已结算则创建退款分销订单
            else {
                //修改分销员提成金额
                distributionService.subCanRebate(CurrencyUtil.sub(0, storeFlow.getDistributionRebate()), distributionOrder.getDistributionId());
            }
        }
    }

}
