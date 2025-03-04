/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.taotaocloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.gateway.springcloud.configuration;

import cn.hutool.http.HttpStatus;
import com.taotao.cloud.common.constant.RedisConstant;
import com.taotao.cloud.common.model.Result;
import com.taotao.cloud.common.utils.common.CaptchaUtil;
import com.taotao.cloud.common.utils.common.JsonUtil;
import com.taotao.cloud.common.utils.context.ContextUtil;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.gateway.springcloud.anti_reptile.constant.AntiReptileConsts;
import com.taotao.cloud.gateway.springcloud.anti_reptile.handler.RefreshFormHandler;
import com.taotao.cloud.gateway.springcloud.anti_reptile.handler.ValidateFormHandler;
import com.taotao.cloud.gateway.springcloud.properties.ApiProperties;
import com.taotao.cloud.health.collect.HealthCheckProvider;
import com.taotao.cloud.health.model.Report;
import com.taotao.cloud.redis.repository.RedisRepository;
import com.wf.captcha.ArithmeticCaptcha;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 特殊路由配置信息
 *
 * @author shuigedeng
 * @version 2022.03
 * @since 2020/4/29 22:11
 */
@Configuration
public class RouterFunctionConfiguration {

	private static final String FALLBACK = "/fallback";
	private static final String CODE = "/code";
	private static final String FAVICON = "/favicon.ico";
	private static final String HEALTH_REPORT = "/health/report";

	@Autowired(required = false)
	private RefreshFormHandler refreshFormHandler;
	@Autowired(required = false)
	ValidateFormHandler validateFormHandler;

	@Bean
	public RouterFunction<ServerResponse> routerFunction(
		FallbackHandler fallbackHandler,
		ImageCodeHandler imageCodeWebHandler,
		FaviconHandler faviconHandler,
		HealthReportHandler healthReportHandler,
		K8sHandler k8sHandler,
		ApiProperties apiProperties) {
		RouterFunction<ServerResponse> routerFunction = RouterFunctions
			.route(RequestPredicates.path(FALLBACK)
				.and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), fallbackHandler)
			.andRoute(RequestPredicates.GET(apiProperties.getBaseUri() + CODE)
				.and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), imageCodeWebHandler)
			.andRoute(RequestPredicates.GET(FAVICON)
				.and(RequestPredicates.accept(MediaType.IMAGE_PNG)), faviconHandler)
			.andRoute(RequestPredicates.GET(HEALTH_REPORT)
				.and(RequestPredicates.accept(MediaType.ALL)), healthReportHandler)
			.andRoute(RequestPredicates.GET("/k8s")
				.and(RequestPredicates.accept(MediaType.ALL)), k8sHandler);

		if (Objects.nonNull(validateFormHandler)) {
			routerFunction.andRoute(RequestPredicates.GET(AntiReptileConsts.VALIDATE_REQUEST_URI)
				.and(RequestPredicates.accept(MediaType.ALL)), validateFormHandler);
		}
		if (Objects.nonNull(refreshFormHandler)) {
			routerFunction.andRoute(RequestPredicates.GET(AntiReptileConsts.REFRESH_REQUEST_URI)
				.and(RequestPredicates.accept(MediaType.ALL)), refreshFormHandler);
		}

		return routerFunction;
	}


	/**
	 * Hystrix 降级处理
	 *
	 * @author shuigedeng
	 * @version 2022.03
	 * @since 2020/4/29 22:11
	 */
	@Component
	public static class FallbackHandler implements HandlerFunction<ServerResponse> {

		private static final int DEFAULT_PORT = 9700;

		@Override
		public Mono<ServerResponse> handle(ServerRequest serverRequest) {
			String originalUris = serverRequest.exchange().getAttribute
				(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
			Optional<InetSocketAddress> socketAddress = serverRequest.remoteAddress();

			Exception exception = serverRequest.exchange()
				.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
			if (exception instanceof TimeoutException) {
				LogUtil.error("服务超时", exception);
			} else if (exception != null && exception.getMessage() != null) {
				LogUtil.error("服务错误" + exception.getMessage(), exception);
			} else {
				LogUtil.error("服务错误", exception);
			}

			LogUtil.error("网关执行请求:{}失败,请求主机: {},请求数据:{} 进行服务降级处理",
				originalUris,
				socketAddress.orElse(new InetSocketAddress(DEFAULT_PORT)).getHostString(),
				buildMessage(serverRequest));

			return ServerResponse
				.status(HttpStatus.HTTP_OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(Result.fail("funciton访问频繁,请稍后重试")));
		}

		private String buildMessage(ServerRequest request) {
			StringBuilder message = new StringBuilder("[");
			message.append(request.methodName());
			message.append(" ");
			message.append(request.uri());
			MultiValueMap<String, String> params = request.queryParams();
			Map<String, String> map = params.toSingleValueMap();
			if (map.size() > 0) {
				message.append(" 请求参数: ");
				String serialize = JsonUtil.toJSONString(message);
				message.append(serialize);
			}
			Object requestBody = request.exchange()
				.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
			if (Objects.nonNull(requestBody)) {
				message.append(" 请求body: ");
				message.append(requestBody);
			}
			message.append("]");
			return message.toString();
		}
	}

	/**
	 * 图形验证码处理器
	 *
	 * @author shuigedeng
	 * @version 2022.03
	 * @since 2020/4/29 22:11
	 */
	@Component
	public static class K8sHandler implements HandlerFunction<ServerResponse> {

		@Override
		public Mono<ServerResponse> handle(ServerRequest request) {
			try {
				String hostName = InetAddress.getLoopbackAddress().getHostAddress();

				return ServerResponse
					.status(HttpStatus.HTTP_OK)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(hostName);
			} catch (Exception e) {
				return ServerResponse
					.status(HttpStatus.HTTP_OK)
					.contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(Result.fail("服务异常,请稍后重试")));
			}
		}
	}


	/**
	 * 图形验证码处理器
	 *
	 * @author shuigedeng
	 * @version 2022.03
	 * @since 2020/4/29 22:11
	 */
	@Component
	public static class ImageCodeHandler implements HandlerFunction<ServerResponse> {

		private static final String PARAM_T = "t";
		private final RedisRepository redisRepository;

		public ImageCodeHandler(RedisRepository redisRepository) {
			this.redisRepository = redisRepository;
		}

		@Override
		public Mono<ServerResponse> handle(ServerRequest request) {
			try {
				ArithmeticCaptcha captcha = CaptchaUtil.getArithmeticCaptcha();
				String text = captcha.text();
				LogUtil.info(text);
				MultiValueMap<String, String> params = request.queryParams();
				String t = params.getFirst(PARAM_T);

				redisRepository
					.setExpire(RedisConstant.CAPTCHA_KEY_PREFIX + t, text.toLowerCase(), 120);

				return ServerResponse
					.status(HttpStatus.HTTP_OK)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(Result.success(captcha.toBase64()));
			} catch (Exception e) {
				return ServerResponse
					.status(HttpStatus.HTTP_OK)
					.contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(Result.fail("服务异常,请稍后重试")));
			}
		}
	}

	/**
	 * 图形验证码处理器
	 *
	 * @author shuigedeng
	 * @version 2022.03
	 * @since 2020/4/29 22:11
	 */
	@Component
	public static class FaviconHandler implements HandlerFunction<ServerResponse> {

		@Override
		public Mono<ServerResponse> handle(ServerRequest request) {
			try {
				ClassPathResource classPathResource = new ClassPathResource("favicon/favicon.ico");
				InputStream inputStream = classPathResource.getInputStream();

				byte[] bytes = IOUtils.toByteArray(inputStream);

				return ServerResponse
					.status(HttpStatus.HTTP_OK)
					.contentType(MediaType.IMAGE_PNG)
					.bodyValue(bytes);
			} catch (Exception e) {
				return ServerResponse
					.status(HttpStatus.HTTP_OK)
					.contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(Result.fail("服务异常,请稍后重试")));
			}
		}
	}

	@Component
	public static class HealthReportHandler implements HandlerFunction<ServerResponse> {

		@Override
		public Mono<ServerResponse> handle(ServerRequest request) {
			try {
				String uri = request.uri().getPath();

				String html;
				HealthCheckProvider healthProvider = ContextUtil.getBean(HealthCheckProvider.class,
					true);
				if (Objects.nonNull(healthProvider) && uri.startsWith(HEALTH_REPORT)) {

					boolean isAnalyse = !"false".equalsIgnoreCase(
						request.queryParam("isAnalyse").orElse("false"));

					Report report = healthProvider.getReport(isAnalyse);
					MediaType mediaType = request.headers().contentType()
						.orElse(MediaType.TEXT_PLAIN);
					if (mediaType.includes(MediaType.APPLICATION_JSON)) {
						return ServerResponse
							.status(HttpStatus.HTTP_OK)
							.contentType(MediaType.APPLICATION_JSON)
							.bodyValue(Result.success(report.toJson()));
					} else {
						html = report
							.toHtml()
							.replace("\r\n", "<br/>")
							.replace("\n", "<br/>")
							.replace("/n", "\n")
							.replace("/r", "\r");
						html = "dump信息:<a href='/health/dump/'>查看</a><br/>" + html;
					}
				} else {
					html = "请配置taotao.cloud.health.enabled=true,taotao.cloud.health.check.enabled=true";
				}

				return ServerResponse
					.status(HttpStatus.HTTP_OK)
					.contentType(MediaType.TEXT_HTML)
					.header("content-type", "text/html;charset=UTF-8")
					.bodyValue(html.getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				return ServerResponse
					.status(HttpStatus.HTTP_OK)
					.contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(Result.fail("服务异常,请稍后重试")));
			}
		}
	}

}
