package com.taotao.cloud.payment.biz.bootx.core.paymodel.alipay.dao;

import com.taotao.cloud.payment.biz.bootx.core.paymodel.alipay.entity.AliPayment;
import com.taotao.cloud.payment.biz.bootx.core.paymodel.base.entity.BasePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**   
* 支付宝
* @author xxm  
* @date 2021/2/26 
*/
@Repository
@RequiredArgsConstructor
public class AliPaymentManager extends BaseManager<AliPaymentMapper, AliPayment> {
    private final AliPaymentMapper aliPaymentRepository;

    public Optional<AliPayment> findByPaymentId(Long paymentId) {
        return this.findByField(BasePayment::getPaymentId,paymentId);
    }
}
