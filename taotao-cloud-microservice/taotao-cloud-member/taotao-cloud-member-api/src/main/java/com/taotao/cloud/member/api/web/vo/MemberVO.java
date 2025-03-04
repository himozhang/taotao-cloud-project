package com.taotao.cloud.member.api.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taotao.cloud.common.enums.ClientTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 会员vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "MemberVO")
public class MemberVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1810890757303309436L;

	@Schema(description = "唯一标识", hidden = true)
	private Long id;

	@Schema(description = "会员用户名")
	private String username;

	@Schema(description = "昵称")
	private String nickName;

	@Schema(description = "会员性别,1为男，0为女")
	private Integer sex;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "会员生日")
	private Date birthday;

	@Schema(description = "会员地址ID")
	private String regionId;

	@Schema(description = "会员地址")
	private String region;

	@Schema(description = "手机号码", required = true)
	//@Sensitive(strategy = SensitiveStrategy.PHONE)
	private String mobile;

	@Schema(description = "积分数量")
	private Long point;

	@Schema(description = "积分总数量")
	private Long totalPoint;

	@Schema(description = "会员头像")
	private String face;

	@Schema(description = "会员状态")
	private Boolean disabled;

	@Schema(description = "是否开通店铺")
	private Boolean haveStore;

	@Schema(description = "店铺ID")
	private String storeId;

	@Schema(description = "openId")
	private String openId;

	/**
	 * @see ClientTypeEnum
	 */
	@Schema(description = "客户端")
	private String clientEnum;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@Schema(description = "最后一次登录时间")
	private Date lastLoginDate;

	@Schema(description = "会员等级ID")
	private String gradeId;

	@Schema(description = "经验值数量")
	private Long experience;
}
