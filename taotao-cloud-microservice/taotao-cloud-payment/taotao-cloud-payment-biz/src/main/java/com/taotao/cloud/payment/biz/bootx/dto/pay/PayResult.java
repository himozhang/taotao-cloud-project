package com.taotao.cloud.payment.biz.bootx.dto.pay;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**   
*
* @author xxm  
* @date 2020/12/9 
*/
@Data
@Accessors(chain = true)
@Schema(title = "支付返回信息")
public class PayResult implements Serializable {

    private static final long serialVersionUID = 7729669194741851195L;

    @Schema(description= "是否是异步支付")
    private boolean asyncPayMode;

    @Schema(description= "异步支付通道")
    private Integer asyncPayChannel;

    /** 主支付记录 */
    @JsonIgnore
    private PaymentInfo payment;

    /**
     * @see TRADE_PROGRESS
     */
    @Schema(description= "支付状态")
    private int payStatus;

    @Schema(description= "异步支付参数")
    private AsyncPayInfo asyncPayInfo;

}
