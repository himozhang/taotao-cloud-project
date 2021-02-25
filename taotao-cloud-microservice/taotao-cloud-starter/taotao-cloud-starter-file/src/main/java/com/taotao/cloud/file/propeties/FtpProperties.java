package com.taotao.cloud.file.propeties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * ftpProperties
 *
 * @author dengtao
 * @since 2020/10/26 09:39
 * @version 1.0.0
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "taotao.cloud.file.ftp")
public class FtpProperties {
    private String host;

    private String port;

    private String username;

    private String password;

    private String domain;

    private String remoteDicrory;
}
