package com.taotao.cloud.sys.biz.model.entity.config;

import java.io.Serializable;
import lombok.Data;

/**
 * 邮箱设置
 */
@Data
public class EmailSetting implements Serializable {

	private static final long serialVersionUID = 7261037221941716140L;

	/**
	 * 邮箱服务器
	 */
	private String host;

	/**
	 * 发送者邮箱账号
	 */
	private String username;

	/**
	 * 邮箱授权码
	 */
	private String password;
}
