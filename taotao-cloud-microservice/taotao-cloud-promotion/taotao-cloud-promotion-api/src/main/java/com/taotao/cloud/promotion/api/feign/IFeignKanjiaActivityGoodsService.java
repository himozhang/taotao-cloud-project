/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.taotaocloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.promotion.api.feign;

import com.taotao.cloud.common.constant.ServiceName;
import com.taotao.cloud.promotion.api.web.dto.KanjiaActivityGoodsDTO;
import com.taotao.cloud.promotion.api.feign.fallback.FeignKanjiaActivityServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * IFeignKanjiaActivityService
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2022-04-07 22:09
 */
@FeignClient( value = ServiceName.TAOTAO_CLOUD_MEMBER_CENTER, fallbackFactory = FeignKanjiaActivityServiceFallback.class)
public interface IFeignKanjiaActivityGoodsService {

	void updateById(KanjiaActivityGoodsDTO kanjiaActivityGoodsDTO);

	KanjiaActivityGoodsDTO getKanjiaGoodsDetail(Long kanjiaActivityGoodsId);
}
