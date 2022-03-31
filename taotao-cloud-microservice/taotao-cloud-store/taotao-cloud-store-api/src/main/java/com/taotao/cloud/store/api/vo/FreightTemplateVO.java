package com.taotao.cloud.store.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 店铺运费模板
 *
 * 
 * @since 2020/11/24 14:29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "店铺运费模板")
//public class FreightTemplateVO extends FreightTemplate {
public class FreightTemplateVO {

	private static final long serialVersionUID = 2422138942308945537L;

	//@Schema(description = "运费详细规则")
	//private List<FreightTemplateChild> freightTemplateChildList;

}
