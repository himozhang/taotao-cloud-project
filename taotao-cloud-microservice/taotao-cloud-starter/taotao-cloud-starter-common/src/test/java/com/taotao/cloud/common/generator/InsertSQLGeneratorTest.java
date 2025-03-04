package com.taotao.cloud.common.generator;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.taotao.cloud.sys.biz.support.generator.InsertSQLGenerator;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

@Test
@Guice(modules = TestConfigModule.class)
public class InsertSQLGeneratorTest {

	private InsertSQLGenerator generator;

	@Inject
	@Named("config.jdbc.driverClass")
	private String className;

	@Inject
	@Named("config.jdbc.url")
	private String url;

	@Inject
	@Named("config.jdbc.username")
	private String username;

	@Inject
	@Named("config.jdbc.password")
	private String password;

	@BeforeTest
	public void setup() {
		try {
			Class.forName(this.className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		this.generator = new InsertSQLGenerator(this.url, this.username,
			this.password, "data_order_ext");
	}

	@AfterTest
	public void destroy() {
		this.generator.close();
	}

	/**
	 * Test method for {@link cn.binarywang.tools.generator.InsertSQLGenerator#generateSQL()}.
	 */
	public void testGenerateSQL() {
		System.err.println(this.generator.generateSQL());
	}

	/**
	 * Test method for {@link cn.binarywang.tools.generator.InsertSQLGenerator#generateParams()} .
	 */
	public void testGenerateParams() {
		System.err.println(this.generator.generateParams());
	}

}
