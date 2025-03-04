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
package com.taotao.cloud.bigdata.hive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 目前先作为一个单独的springboot项目
 * <p>
 * 之后如果有需要可以添加到toatoa cloud中 作为一个资源服务器( 添加依赖 添加注解 添加配置)
 *
 * @author shuigedeng
 * @version 2022.04
 * @since 2020/10/30 16:06
 */
@SpringBootApplication
public class BigdataHiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(BigdataHiveApplication.class, args);
	}

}
