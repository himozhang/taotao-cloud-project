package com.taotao.cloud.office.execl.core.handler;

import com.alibaba.excel.annotation.ExcelProperty;


public class WriteHandlerData {
    @ExcelProperty("姓名")
    private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
