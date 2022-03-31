package com.taotao.cloud.promotion.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会员dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    /**
     * 会员昵称
     */
    private String nickName;
    /**
     * id
     */
    private String id;
}
