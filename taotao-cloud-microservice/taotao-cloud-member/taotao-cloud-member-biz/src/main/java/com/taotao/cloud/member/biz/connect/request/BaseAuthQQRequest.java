package com.taotao.cloud.member.biz.connect.request;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.taotao.cloud.common.utils.io.UrlBuilder;
import com.taotao.cloud.common.utils.lang.StringUtil;
import com.taotao.cloud.member.biz.connect.config.AuthConfig;
import com.taotao.cloud.member.biz.connect.config.ConnectAuthEnum;
import com.taotao.cloud.member.biz.connect.entity.dto.AuthCallback;
import com.taotao.cloud.member.biz.connect.entity.dto.AuthResponse;
import com.taotao.cloud.member.biz.connect.entity.dto.AuthToken;
import com.taotao.cloud.member.biz.connect.entity.dto.ConnectAuthUser;
import com.taotao.cloud.member.biz.connect.entity.enums.AuthResponseStatus;
import com.taotao.cloud.member.biz.connect.entity.enums.AuthUserGender;
import com.taotao.cloud.member.biz.connect.exception.AuthException;
import com.taotao.cloud.member.biz.connect.util.GlobalAuthUtils;
import com.taotao.cloud.redis.repository.RedisRepository;

import java.util.Map;

/**
 * qq登录
 */
public class BaseAuthQQRequest extends BaseAuthRequest {

	public BaseAuthQQRequest(AuthConfig config, RedisRepository redisRepository) {
		super(config, ConnectAuthEnum.QQ, redisRepository);
	}

	@Override
	protected AuthToken getAccessToken(AuthCallback authCallback) {
		String response = doGetAuthorizationCode(authCallback.getCode());
		return getAuthToken(response);
	}

	@Override
	public AuthResponse refresh(AuthToken authToken) {
		//todo 此处已修改
		// String response = new HttpUtils(config.getHttpConfig()).get(refreshTokenUrl(authToken.getRefreshToken()));
		String response = "";
		return AuthResponse.builder().code(AuthResponseStatus.SUCCESS.getCode()).data(getAuthToken(response)).build();
	}

	@Override
	protected ConnectAuthUser getUserInfo(AuthToken authToken) {
		String openId = this.getOpenId(authToken);
		String response = doGetUserInfo(authToken);
		JSONObject object = JSONObject.parseObject(response);
		if (object.getIntValue("ret") != 0) {
			throw new AuthException(object.getString("msg"));
		}
		String avatar = object.getString("figureurl_qq_2");
		if (StringUtil.isEmpty(avatar)) {
			avatar = object.getString("figureurl_qq_1");
		}

		String location = String.format("%s-%s", object.getString("province"), object.getString("city"));
		return ConnectAuthUser.builder()
			.rawUserInfo(object)
			.username(object.getString("nickname"))
			.nickname(object.getString("nickname"))
			.avatar(avatar)
			.location(location)
			.uuid(openId)
			.gender(AuthUserGender.getRealGender(object.getString("gender")))
			.token(authToken)
			.source(source.toString())
			.build();
	}

	/**
	 * 获取QQ用户的OpenId，支持自定义是否启用查询unionid的功能，如果启用查询unionid的功能，
	 * 那就需要开发者先通过邮件申请unionid功能，参考链接 {@see http://wiki.connect.qq.com/unionid%E4%BB%8B%E7%BB%8D}
	 *
	 * @param authToken 通过{@link BaseAuthQQRequest#getAccessToken(AuthCallback)}获取到的{@code authToken}
	 * @return openId
	 */
	private String getOpenId(AuthToken authToken) {
		String response = new HttpUtils(config.getHttpConfig()).get(UrlBuilder.fromBaseUrl("https://graph.qq.com/oauth2.0/me")
			.queryParam("access_token", authToken.getAccessToken())
			.queryParam("unionid", config.isUnionId() ? 1 : 0)
			.build());
		String removePrefix = response.replace("callback(", "");
		String removeSuffix = removePrefix.replace(");", "");
		String openId = removeSuffix.trim();
		JSONObject object = JSONObject.parseObject(openId);
		if (object.containsKey("error")) {
			throw new AuthException(object.get("error") + ":" + object.get("error_description"));
		}
		authToken.setOpenId(object.getString("openid"));
		if (object.containsKey("unionid")) {
			authToken.setUnionId(object.getString("unionid"));
		}
		return StringUtil.isEmpty(authToken.getUnionId()) ? authToken.getOpenId() : authToken.getUnionId();
	}

	/**
	 * 返回获取userInfo的url
	 *
	 * @param authToken 用户授权token
	 * @return 返回获取userInfo的url
	 */
	@Override
	protected String userInfoUrl(AuthToken authToken) {
		return UrlBuilder.fromBaseUrl(source.userInfo())
			.queryParam("access_token", authToken.getAccessToken())
			.queryParam("oauth_consumer_key", config.getClientId())
			.queryParam("openid", authToken.getOpenId())
			.build();
	}

	private AuthToken getAuthToken(String response) {
		Map<String, String> accessTokenObject = GlobalAuthUtils.parseStringToMap(response);
		if (!accessTokenObject.containsKey("access_token") || accessTokenObject.containsKey("code")) {
			throw new AuthException(accessTokenObject.get("msg"));
		}
		return AuthToken.builder()
			.accessToken(accessTokenObject.get("access_token"))
			.expireIn(Convert.toInt(accessTokenObject.getOrDefault("expires_in", "0")))
			.refreshToken(accessTokenObject.get("refresh_token"))
			.build();
	}

	@Override
	public String authorize(String state) {
		return UrlBuilder.fromBaseUrl(super.authorize(state))
			.queryParam("scope", "get_user_info")
			.build();
	}
}
