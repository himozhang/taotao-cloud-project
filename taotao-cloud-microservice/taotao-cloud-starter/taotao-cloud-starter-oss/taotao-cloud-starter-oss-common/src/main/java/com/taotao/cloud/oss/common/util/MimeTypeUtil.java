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
package com.taotao.cloud.oss.common.util;

/**
 * 媒体类型工具类
 *
 * @author shuigedeng
 * @version 2022.03
 * @since 2020/10/26 10:45
 */
public class MimeTypeUtil {

	public static final String IMAGE_PNG = "image/png";

	public static final String IMAGE_JPG = "image/jpg";

	public static final String IMAGE_JPEG = "image/jpeg";

	public static final String IMAGE_BMP = "image/bmp";

	public static final String IMAGE_GIF = "image/gif";

	public static final String[] IMAGE_EXTENSION = {"bmp", "gif", "jpg", "jpeg", "png"};

	public static final String[] FLASH_EXTENSION = {"swf", "flv"};

	public static final String[] MEDIA_EXTENSION = {"swf", "flv", "mp3", "wav", "wma", "wmv", "mid",
		"avi", "mpg",
		"asf", "rm", "rmvb"};

	public static final String[] DEFAULT_ALLOWED_EXTENSION = {
		// 图片
		"bmp", "gif", "jpg", "jpeg", "png",
		// word excel powerpoint
		"doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
		// 压缩文件
		"rar", "zip", "gz", "bz2",
		// pdf
		"pdf"};

	public static String getExtension(String prefix) {
		return switch (prefix) {
			case IMAGE_PNG -> "png";
			case IMAGE_JPG -> "jpg";
			case IMAGE_JPEG -> "jpeg";
			case IMAGE_BMP -> "bmp";
			case IMAGE_GIF -> "gif";
			default -> "";
		};
	}
}
