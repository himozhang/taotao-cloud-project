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

package com.taotao.cloud.web.xss;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.taotao.cloud.common.utils.log.LogUtil;
import com.taotao.cloud.web.properties.XssProperties;
import com.taotao.cloud.web.utils.XssUtil;
import java.beans.PropertyEditorSupport;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 表单 xss 处理
 *
 * @author shuigedeng
 * @version 2021.9
 * @since 2021-09-02 20:01:42
 */
public class FormXssClean {

	private final XssProperties properties;
	private final XssCleaner xssCleaner;

	public FormXssClean(XssProperties properties, XssCleaner xssCleaner) {
		this.properties = properties;
		this.xssCleaner = xssCleaner;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// 处理前端传来的表单字符串
		binder.registerCustomEditor(String.class, new StringPropertiesEditor(xssCleaner, properties));
	}

	/**
	 * FormXssClean
	 *
	 * @author shuigedeng
	 * @version 2022.03
	 * @since 2022-03-25 15:10:33
	 */
	public static class StringPropertiesEditor extends PropertyEditorSupport {

		private final XssCleaner xssCleaner;
		private final XssProperties properties;

		public StringPropertiesEditor(XssCleaner xssCleaner,
			XssProperties properties) {
			this.xssCleaner = xssCleaner;
			this.properties = properties;
		}

		public StringPropertiesEditor(Object source, XssCleaner xssCleaner,
			XssProperties properties) {
			super(source);

			this.xssCleaner = xssCleaner;
			this.properties = properties;
		}

		@Override
		public String getAsText() {
			Object value = getValue();
			return value != null ? value.toString() : StringPool.EMPTY;
		}

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if (text == null) {
				setValue(null);
			} else if (XssHolder.isEnabled()) {
				String value = xssCleaner.clean(XssUtil.trim(text, properties.getTrimText()));
				setValue(value);
				LogUtil.debug("Request parameter value:{} cleaned up by xss, current value is:{}.", text, value);
			} else {
				setValue(XssUtil.trim(text, properties.getTrimText()));
			}
		}
	}

}
