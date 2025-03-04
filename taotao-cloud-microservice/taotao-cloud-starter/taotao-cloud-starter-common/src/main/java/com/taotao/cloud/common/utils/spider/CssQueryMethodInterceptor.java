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
package com.taotao.cloud.common.utils.spider;

import com.taotao.cloud.common.utils.convert.ConvertUtil;
import com.taotao.cloud.common.utils.lang.StringUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

/**
 * 代理模型
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 20:01:42
 */
public class CssQueryMethodInterceptor implements MethodInterceptor {

	private final Class<?> clazz;
	private final Element element;


	public CssQueryMethodInterceptor(Class<?> clazz, Element element) {
		this.clazz = clazz;
		this.element = element;
	}

	@Nullable
	@Override
	public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy)
		throws Throwable {
		// 如果是 toString eq 等方法都不准确，故直接返回死值
		if (ReflectionUtils.isObjectMethod(method)) {
			return methodProxy.invokeSuper(object, args);
		}
		// 非 bean 方法
		PropertyDescriptor propertyDescriptor = BeanUtils.findPropertyForMethod(method, clazz);
		if (propertyDescriptor == null) {
			return methodProxy.invokeSuper(object, args);
		}
		// 非 read 的方法，只处理 get 方法 is
		if (!method.equals(propertyDescriptor.getReadMethod())) {
			return methodProxy.invokeSuper(object, args);
		}
		// 兼容 lombok bug 强制首字母小写： https://github.com/rzwitserloot/lombok/issues/1861
		String fieldName = StringUtil.firstCharToLower(propertyDescriptor.getDisplayName());
		Field field = clazz.getDeclaredField(fieldName);
		if (field == null) {
			return methodProxy.invokeSuper(object, args);
		}
		CssQuery cssQuery = field.getAnnotation(CssQuery.class);
		// 没有注解，不代理
		if (cssQuery == null) {
			return methodProxy.invokeSuper(object, args);
		}
		Class<?> returnType = method.getReturnType();
		boolean isColl = Collection.class.isAssignableFrom(returnType);
		String cssQueryValue = cssQuery.value();
		// 是否为 bean 中 bean
		boolean isInner = cssQuery.inner();
		if (isInner) {
			return proxyInner(cssQueryValue, method, returnType, isColl);
		}
		Object proxyValue = proxyValue(cssQueryValue, cssQuery, returnType, isColl);
		if (String.class.isAssignableFrom(returnType)) {
			return proxyValue;
		}
		// 用于读取 field 上的注解
		TypeDescriptor typeDescriptor = new TypeDescriptor(field);
		return ConvertUtil.convert(proxyValue, typeDescriptor);
	}

	@Nullable
	private Object proxyValue(String cssQueryValue, CssQuery cssQuery, Class<?> returnType,
		boolean isColl) {
		if (isColl) {
			Elements elements = Selector.select(cssQueryValue, element);
			Collection<Object> valueList = newColl(returnType);
			if (elements.isEmpty()) {
				return valueList;
			}
			for (Element select : elements) {
				String value = getValue(select, cssQuery);
				if (value != null) {
					valueList.add(value);
				}
			}
			return valueList;
		}
		Element select = Selector.selectFirst(cssQueryValue, element);
		return getValue(select, cssQuery);
	}

	private Object proxyInner(String cssQueryValue, Method method, Class<?> returnType,
		boolean isColl) {
		if (isColl) {
			Elements elements = Selector.select(cssQueryValue, element);
			Collection<Object> valueList = newColl(returnType);
			ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
			Class<?> innerType = resolvableType.getGeneric(0).resolve();
			if (innerType == null) {
				throw new IllegalArgumentException("Class " + returnType + " 读取泛型失败。");
			}
			for (Element select : elements) {
				valueList.add(DomMapper.readValue(select, innerType));
			}
			return valueList;
		}
		Element select = Selector.selectFirst(cssQueryValue, element);
		return DomMapper.readValue(select, returnType);
	}

	@Nullable
	private String getValue(@Nullable Element element, CssQuery cssQuery) {
		if (element == null) {
			return null;
		}
		// 读取的属性名
		String attrName = cssQuery.attr();
		// 读取的值
		String attrValue;
		if (StringUtil.isBlank(attrName)) {
			attrValue = element.outerHtml();
		} else if ("html".equalsIgnoreCase(attrName)) {
			attrValue = element.html();
		} else if ("text".equalsIgnoreCase(attrName)) {
			attrValue = getText(element);
		} else if ("allText".equalsIgnoreCase(attrName)) {
			attrValue = element.text();
		} else {
			attrValue = element.attr(attrName);
		}
		// 判断是否需要正则处理
		String regex = cssQuery.regex();
		if (StringUtil.isBlank(attrValue) || StringUtil.isBlank(regex)) {
			return attrValue;
		}
		// 处理正则表达式
		return getRegexValue(regex, cssQuery.regexGroup(), attrValue);
	}

	@Nullable
	private String getRegexValue(String regex, int regexGroup, String value) {
		// 处理正则表达式
		Matcher matcher = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE)
			.matcher(value);
		if (!matcher.find()) {
			return null;
		}
		// 正则 group
		if (regexGroup > CssQuery.DEFAULT_REGEX_GROUP) {
			return matcher.group(regexGroup);
		}
		return matcher.group();
	}

	private String getText(Element element) {
		return element.childNodes().stream()
			.filter(node -> node instanceof TextNode)
			.map(node -> (TextNode) node)
			.map(TextNode::text)
			.collect(Collectors.joining());
	}

	private Collection<Object> newColl(Class<?> returnType) {
		return Set.class.isAssignableFrom(returnType) ? new HashSet<>() : new ArrayList<>();
	}
}
