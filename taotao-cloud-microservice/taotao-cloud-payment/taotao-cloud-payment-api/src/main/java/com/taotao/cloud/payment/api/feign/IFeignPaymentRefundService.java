package com.taotao.cloud.payment.api.feign;

import com.taotao.cloud.common.constant.ServiceName;
import com.taotao.cloud.payment.api.feign.fallback.FeignRefundLogServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 远程调用快递公司模块
 *
 * @author shuigedeng
 * @since 2020/5/2 16:42
 */
@FeignClient(value = ServiceName.TAOTAO_CLOUD_LOGISTICS_CENTER, fallbackFactory = FeignRefundLogServiceFallback.class)
public interface IFeignPaymentRefundService {

}

