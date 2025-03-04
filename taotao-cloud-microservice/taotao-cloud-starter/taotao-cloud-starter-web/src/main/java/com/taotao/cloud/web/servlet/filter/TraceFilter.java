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
package com.taotao.cloud.web.servlet.filter;


import com.taotao.cloud.common.context.TraceContextHolder;
import com.taotao.cloud.common.utils.common.IdGeneratorUtil;
import com.taotao.cloud.common.utils.lang.StringUtil;
import com.taotao.cloud.common.utils.servlet.RequestUtil;
import com.taotao.cloud.common.utils.servlet.TraceUtil;
import com.taotao.cloud.web.properties.FilterProperties;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 日志链路追踪过滤器
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 22:16:19
 */
@WebFilter(filterName = "TraceFilter", urlPatterns = "/*", asyncSupported = true)
public class TraceFilter extends OncePerRequestFilter {

	private final FilterProperties filterProperties;

	public TraceFilter(FilterProperties filterProperties) {
		this.filterProperties = filterProperties;
	}

	//@Override
	//protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	//	if (request.getRequestURI().startsWith("/actuator")) {
	//		return true;
	//	}
	//	return !filterProperties.getTrace();
	//}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return RequestUtil.excludeActuator(request);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String traceId = TraceUtil.getTraceId(request);
			if(StringUtil.isBlank(traceId)){
				traceId = IdGeneratorUtil.getIdStr();
			}
			TraceContextHolder.setTraceId(traceId);

			TraceUtil.mdcTraceId(traceId);
			TraceUtil.mdcZipkinTraceId(request);
			TraceUtil.mdcZipkinSpanId(request);

			filterChain.doFilter(request, response);
		} finally {
			TraceContextHolder.clear();
			TraceUtil.mdcRemoveTraceId();
			TraceUtil.mdcRemoveZipkinTraceId();
			TraceUtil.mdcRemoveZipkinSpanId();
		}
	}
}
