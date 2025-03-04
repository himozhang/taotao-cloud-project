package com.taotao.cloud.third.client.support.forest;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Var;
import com.taotao.cloud.third.client.support.forest.auth.MyAuth;

public interface MyClient {

	@Request("http://localhost:8080/hello")
	String helloForest();

	/**
	 * 在请求接口上加上自定义的 @MyAuth 注解
	 * 注解的参数可以是字符串模板，通过方法调用的时候动态传入
	 * 也可以是写死的字符串
	 */
	@Get("/hello/user?username={username}")
	@MyAuth(username = "{username}", password = "bar")
	String send(@Var("username") String username);
}
