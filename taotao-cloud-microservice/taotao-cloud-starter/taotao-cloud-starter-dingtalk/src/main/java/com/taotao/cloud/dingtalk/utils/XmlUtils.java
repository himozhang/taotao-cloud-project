/*
 * Copyright (c) ©2015-2021 Jaemon. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taotao.cloud.dingtalk.utils;

import static com.taotao.cloud.dingtalk.constant.DingerConstant.NEW_LINE;

import jakarta.xml.bind.JAXB;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;


/**
 * XML字符串JavaBean对象互转工具类
 *
 * @author shuigedeng
 * @version 2022.07
 * @since 2022-07-06 15:26:43
 */
public class XmlUtils {

	private XmlUtils() {
	}

	/**
	 * xmlString to javaBean
	 *
	 * @param xmlString xmlString
	 * @param clazz     clazz
	 * @param <T>       T
	 * @return javaBean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xmlToJavaBean(String xmlString, Class<T> clazz) {
		return (T) JAXB.unmarshal(new StringReader(xmlString), clazz);
	}

	/**
	 * xml list String to javaBean
	 *
	 * @param xmlString xmlString
	 * @param clazz     clazz
	 * @param <T>       T
	 * @return javaBean
	 */
	public static <T> T xmlToJavaBean(List<String> xmlString, Class<T> clazz) {
		return (T) JAXB.unmarshal(new StringReader(String.join(NEW_LINE, xmlString)), clazz);
	}

	/**
	 * javaBean to xmlString
	 *
	 * @param bean bean
	 * @param <T>  T
	 * @return xmlString
	 */
	public static <T> String javaBeanToXML(T bean) {
		Writer writer = new StringWriter();
		JAXB.marshal(bean, writer);
		return writer.toString();
	}

}
