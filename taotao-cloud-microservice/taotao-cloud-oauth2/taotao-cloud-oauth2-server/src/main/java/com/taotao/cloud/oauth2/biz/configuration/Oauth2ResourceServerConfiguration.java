package com.taotao.cloud.oauth2.biz.configuration;

import com.taotao.cloud.common.utils.LogUtil;
import com.taotao.cloud.oauth2.biz.models.CustomJwtGrantedAuthoritiesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableGlobalMethodSecurity(
	prePostEnabled = true,
	order = 0
)
@EnableWebSecurity
public class Oauth2ResourceServerConfiguration {

	@Value("${jwk.set.uri}")
	private String jwkSetUri;

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
	}

	JwtAuthenticationConverter jwtAuthenticationConverter() {
		CustomJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new CustomJwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthorityPrefix("");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests(authorizeRequests -> authorizeRequests
				.mvcMatchers("/messages/**").access("hasAuthority('SCOPE_message.read')")
				.antMatchers("/favicon.ico ").permitAll()
				.anyRequest().authenticated()
			)
			.anonymous().disable()
			.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					LogUtil.info("认证失败");
				})
				.authenticationEntryPoint((request, reponse, ex) -> {
					LogUtil.info("认证失败---------------");
				})
				.jwt(jwt -> jwt.decoder(jwtDecoder())
					.jwtAuthenticationConverter(jwtAuthenticationConverter()))
			);

		return http.build();
	}

}
