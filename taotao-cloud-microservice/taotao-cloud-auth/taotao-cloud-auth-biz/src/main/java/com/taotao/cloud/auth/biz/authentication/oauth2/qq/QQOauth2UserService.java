package com.taotao.cloud.auth.biz.authentication.oauth2.qq;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.cloud.common.utils.log.LogUtil;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class QQOauth2UserService extends DefaultOAuth2UserService {

	private static final String QQ = "qq";
	private static final String QQ_OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me";

	private final RestTemplate restTemplate = new RestTemplate();

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		ClientRegistration clientRegistration = userRequest.getClientRegistration();
		String registrationId = clientRegistration.getRegistrationId();

		// 获取qq用户信息的流程很奇葩需要我们自定义
		if (QQ.equals(registrationId)) {
			String tokenValue = userRequest.getAccessToken().getTokenValue();
			// openId请求
			RequestEntity<?> openIdRequest = RequestEntity.get(
				UriComponentsBuilder.fromUriString(QQ_OPEN_ID_URL)
					.queryParam("access_token", tokenValue)
					.build()
					.toUri()).build();

			// openId响应
			ResponseEntity<String> openIdResponse = restTemplate
				.exchange(openIdRequest, new ParameterizedTypeReference<String>() {
				});

			LogUtil.info("qq的openId响应信息：{}", openIdResponse);

			// openId响应是类似callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} );这样的字符串
			String openId = null;
			try {
				openId = extractQqOpenId(Objects.requireNonNull(openIdResponse.getBody()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			// userInfo请求
			RequestEntity<?> userInfoRequest = RequestEntity.get(UriComponentsBuilder.fromUriString(
				clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
				.queryParam("access_token", tokenValue)
				.queryParam("openid", openId)
				.queryParam("oauth_consumer_key", clientRegistration.getClientId())
				.build()
				.toUri()).build();

			// userInfo响应
			ResponseEntity<String> userInfoResponse = restTemplate
				.exchange(userInfoRequest, new ParameterizedTypeReference<String>() {
				});

			LogUtil.info("qq的userInfo响应信息：{}", userInfoResponse);

			String userNameAttributeName = clientRegistration.getProviderDetails()
				.getUserInfoEndpoint().getUserNameAttributeName();
			Map<String, Object> userAttributes = null;
			try {
				userAttributes = extractQqUserInfo(
					Objects.requireNonNull(userInfoResponse.getBody()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			Set<GrantedAuthority> authorities = new LinkedHashSet<>();
			authorities.add(new OAuth2UserAuthority(userAttributes));
			OAuth2AccessToken token = userRequest.getAccessToken();
			for (String authority : token.getScopes()) {
				authorities.add(new SimpleGrantedAuthority("SCOPE_" + authority));
			}
			return new DefaultOAuth2User(authorities, userAttributes, userNameAttributeName);
		}
		return super.loadUser(userRequest);
	}


	/**
	 * 提取qq的openId
	 *
	 * @param openIdResponse qq的openId响应字符串
	 * @return qq的openId
	 */
	private String extractQqOpenId(String openIdResponse) throws JsonProcessingException {
		String openId = openIdResponse
			.substring(openIdResponse.indexOf('(') + 1, openIdResponse.indexOf(')'));
		Map<String, String> map = objectMapper
			.readValue(openId, new TypeReference<Map<String, String>>() {
			});
		return map.get("openid");
	}

	/**
	 * 提取qq的用户信息
	 *
	 * @param userInfoResponse qq的用户信息响应字符串
	 * @return qq的用户信息
	 */
	private Map<String, Object> extractQqUserInfo(String userInfoResponse)
		throws JsonProcessingException {
		return objectMapper.readValue(userInfoResponse, new TypeReference<Map<String, Object>>() {
		});
	}

}
