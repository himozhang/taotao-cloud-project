//package com.taotao.cloud.auth.api.tmp.social.converter;
//
//import com.taotao.cloud.auth.api.tmp.social.user.WechatOAuth2User;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Consumer;
//import javax.servlet.http.HttpServletRequest;
//import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
//import org.springframework.security.crypto.keygen.StringKeyGenerator;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
//import org.springframework.security.oauth2.core.AuthorizationGrantType;
//import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
//import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
//import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
//import org.springframework.security.oauth2.core.oidc.OidcScopes;
//import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
//import org.springframework.security.web.util.UrlUtils;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.util.Assert;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//import org.springframework.web.util.UriComponents;
//import org.springframework.web.util.UriComponentsBuilder;
//
///**
// * 解决微信OAuth2.0 字段名不一样
// */
//public final class CustomOAuth2AuthorizationRequestResolver implements
//	OAuth2AuthorizationRequestResolver {
//
//	private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";
//	private static final char PATH_DELIMITER = '/';
//	private final ClientRegistrationRepository clientRegistrationRepository;
//	private final AntPathRequestMatcher authorizationRequestMatcher;
//	private final StringKeyGenerator stateGenerator = new Base64StringKeyGenerator(
//		Base64.getUrlEncoder());
//	private final StringKeyGenerator secureKeyGenerator = new Base64StringKeyGenerator(
//		Base64.getUrlEncoder().withoutPadding(), 96);
//	private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer = customizer -> {
//	};
//
//	/**
//	 * Constructs a {@code DefaultOAuth2AuthorizationRequestResolver} using the provided
//	 * parameters.
//	 *
//	 * @param clientRegistrationRepository the repository of client registrations
//	 * @param authorizationRequestBaseUri  the base {@code URI} used for resolving authorization
//	 *                                     requests
//	 */
//	public CustomOAuth2AuthorizationRequestResolver(
//		ClientRegistrationRepository clientRegistrationRepository,
//		String authorizationRequestBaseUri) {
//		Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
//		Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
//		this.clientRegistrationRepository = clientRegistrationRepository;
//		this.authorizationRequestMatcher = new AntPathRequestMatcher(
//			authorizationRequestBaseUri + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
//	}
//
//	@Override
//	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
//		String registrationId = this.resolveRegistrationId(request);
//		String redirectUriAction = getAction(request, "login");
//		return resolve(request, registrationId, redirectUriAction);
//	}
//
//	@Override
//	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId) {
//		if (registrationId == null) {
//			return null;
//		}
//		String redirectUriAction = getAction(request, "authorize");
//		return resolve(request, registrationId, redirectUriAction);
//	}
//
//	/**
//	 * Sets the {@code Consumer} to be provided the {@link OAuth2AuthorizationRequest.Builder}
//	 * allowing for further customizations.
//	 *
//	 * @param authorizationRequestCustomizer the {@code Consumer} to be provided the {@link
//	 *                                       OAuth2AuthorizationRequest.Builder}
//	 */
//	public void setAuthorizationRequestCustomizer(
//		Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer) {
//		Assert.notNull(authorizationRequestCustomizer,
//			"authorizationRequestCustomizer cannot be null");
//		this.authorizationRequestCustomizer = authorizationRequestCustomizer;
//	}
//
//	private String getAction(HttpServletRequest request, String defaultAction) {
//		String action = request.getParameter("action");
//		if (action == null) {
//			return defaultAction;
//		}
//		return action;
//	}
//
//	private OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId,
//		String redirectUriAction) {
//		if (registrationId == null) {
//			return null;
//		}
//
//		ClientRegistration clientRegistration = this.clientRegistrationRepository
//			.findByRegistrationId(registrationId);
//		if (clientRegistration == null) {
//			throw new IllegalArgumentException(
//				"Invalid Client Registration with Id: " + registrationId);
//		}
//
//		Map<String, Object> attributes = new HashMap<>();
//		attributes
//			.put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId());
//
//		OAuth2AuthorizationRequest.Builder builder;
//		if (AuthorizationGrantType.AUTHORIZATION_CODE
//			.equals(clientRegistration.getAuthorizationGrantType())) {
//			builder = OAuth2AuthorizationRequest.authorizationCode();
//			Map<String, Object> additionalParameters = new HashMap<>();
//			// 解决微信OAuth2.0 字段名不一样
//			additionalParameters.put(WechatOAuth2User.APP_ID, clientRegistration.getClientId());
//			if (!CollectionUtils.isEmpty(clientRegistration.getScopes()) &&
//				clientRegistration.getScopes().contains(OidcScopes.OPENID)) {
//				// Section 3.1.2.1 Authentication Request - https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
//				// scope
//				// 		REQUIRED. OpenID Connect requests MUST contain the "openid" scope value.
//				addNonceParameters(attributes, additionalParameters);
//			}
//			if (ClientAuthenticationMethod.NONE
//				.equals(clientRegistration.getClientAuthenticationMethod())) {
//				addPkceParameters(attributes, additionalParameters);
//			}
//			builder.additionalParameters(additionalParameters);
//		} else if (AuthorizationGrantType.IMPLICIT
//			.equals(clientRegistration.getAuthorizationGrantType())) {
//			builder = OAuth2AuthorizationRequest.implicit();
//		} else {
//			throw new IllegalArgumentException("Invalid Authorization Grant Type (" +
//				clientRegistration.getAuthorizationGrantType().getValue() +
//				") for Client Registration with Id: " + clientRegistration.getRegistrationId());
//		}
//
//		String redirectUriStr = expandRedirectUri(request, clientRegistration, redirectUriAction);
//
//		builder
//			.clientId(clientRegistration.getClientId())
//			.authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
//			.redirectUri(redirectUriStr)
//			.scopes(clientRegistration.getScopes())
//			.state(this.stateGenerator.generateKey())
//			.attributes(attributes);
//
//		this.authorizationRequestCustomizer.accept(builder);
//
//		return builder.build();
//	}
//
//	private String resolveRegistrationId(HttpServletRequest request) {
//		if (this.authorizationRequestMatcher.matches(request)) {
//			return this.authorizationRequestMatcher
//				.matcher(request).getVariables().get(REGISTRATION_ID_URI_VARIABLE_NAME);
//		}
//		return null;
//	}
//
//	/**
//	 * Expands the {@link ClientRegistration#getRedirectUriTemplate()} with following provided
//	 * variables:<br/> - baseUrl (e.g. https://localhost/app) <br/> - baseScheme (e.g. https) <br/>
//	 * - baseHost (e.g. localhost) <br/> - basePort (e.g. :8080) <br/> - basePath (e.g. /app) <br/>
//	 * - registrationId (e.g. google) <br/> - action (e.g. login) <br/>
//	 * <p/>
//	 * Null variables are provided as empty strings.
//	 * <p/>
//	 * Default redirectUriTemplate is: {@link org.springframework.security.config.oauth2.client}.CommonOAuth2Provider#DEFAULT_REDIRECT_URL
//	 *
//	 * @return expanded URI
//	 */
//	private static String expandRedirectUri(HttpServletRequest request,
//		ClientRegistration clientRegistration, String action) {
//		Map<String, String> uriVariables = new HashMap<>();
//		uriVariables.put("registrationId", clientRegistration.getRegistrationId());
//
//		UriComponents uriComponents = UriComponentsBuilder
//			.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
//			.replacePath(request.getContextPath())
//			.replaceQuery(null)
//			.fragment(null)
//			.build();
//		String scheme = uriComponents.getScheme();
//		uriVariables.put("baseScheme", scheme == null ? "" : scheme);
//		String host = uriComponents.getHost();
//		uriVariables.put("baseHost", host == null ? "" : host);
//		// following logic is based on HierarchicalUriComponents#toUriString()
//		int port = uriComponents.getPort();
//		uriVariables.put("basePort", port == -1 ? "" : ":" + port);
//		String path = uriComponents.getPath();
//		if (StringUtils.hasLength(path)) {
//			if (path.charAt(0) != PATH_DELIMITER) {
//				path = PATH_DELIMITER + path;
//			}
//		}
//		uriVariables.put("basePath", path == null ? "" : path);
//		uriVariables.put("baseUrl", uriComponents.toUriString());
//
//		uriVariables.put("action", action == null ? "" : action);
//
//		return UriComponentsBuilder.fromUriString(clientRegistration.getRedirectUriTemplate())
//			.buildAndExpand(uriVariables)
//			.toUriString();
//	}
//
//	/**
//	 * Creates nonce and its hash for use in OpenID Connect 1.0 Authentication Requests.
//	 *
//	 * @param attributes           where the {@link OidcParameterNames#NONCE} is stored for the
//	 *                             authentication request
//	 * @param additionalParameters where the {@link OidcParameterNames#NONCE} hash is added for the
//	 *                             authentication request
//	 * @see <a target="_blank" href="https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest">3.1.2.1.
//	 * Authentication Request</a>
//	 * @since 5.2
//	 */
//	private void addNonceParameters(Map<String, Object> attributes,
//		Map<String, Object> additionalParameters) {
//		try {
//			String nonce = this.secureKeyGenerator.generateKey();
//			String nonceHash = createHash(nonce);
//			attributes.put(OidcParameterNames.NONCE, nonce);
//			additionalParameters.put(OidcParameterNames.NONCE, nonceHash);
//		} catch (NoSuchAlgorithmException e) {
//		}
//	}
//
//	/**
//	 * Creates and adds additional PKCE parameters for use in the OAuth 2.0 Authorization and Access
//	 * Token Requests
//	 *
//	 * @param attributes           where {@link PkceParameterNames#CODE_VERIFIER} is stored for the
//	 *                             token request
//	 * @param additionalParameters where {@link PkceParameterNames#CODE_CHALLENGE} and, usually,
//	 *                             {@link PkceParameterNames#CODE_CHALLENGE_METHOD} are added to be
//	 *                             used in the authorization request.
//	 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7636#section-1.1">1.1.  Protocol
//	 * Flow</a>
//	 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7636#section-4.1">4.1.  Client
//	 * Creates a Code Verifier</a>
//	 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7636#section-4.2">4.2.  Client
//	 * Creates the Code Challenge</a>
//	 * @since 5.2
//	 */
//	private void addPkceParameters(Map<String, Object> attributes,
//		Map<String, Object> additionalParameters) {
//		String codeVerifier = this.secureKeyGenerator.generateKey();
//		attributes.put(PkceParameterNames.CODE_VERIFIER, codeVerifier);
//		try {
//			String codeChallenge = createHash(codeVerifier);
//			additionalParameters.put(PkceParameterNames.CODE_CHALLENGE, codeChallenge);
//			additionalParameters.put(PkceParameterNames.CODE_CHALLENGE_METHOD, "S256");
//		} catch (NoSuchAlgorithmException e) {
//			additionalParameters.put(PkceParameterNames.CODE_CHALLENGE, codeVerifier);
//		}
//	}
//
//	private static String createHash(String value) throws NoSuchAlgorithmException {
//		MessageDigest md = MessageDigest.getInstance("SHA-256");
//		byte[] digest = md.digest(value.getBytes(StandardCharsets.US_ASCII));
//		return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
//	}
//}
