package com.taotao.cloud.oss.artislong.model;


/**
 * 文件信息对象
 *
 * @author 陈敏
 * @version FileInfo.java, v 1.1 2021/11/15 10:19 chenmin Exp $ Created on 2021/11/15
 */
public class FileOssInfo extends OssInfo {

	private String id;

	public FileOssInfo() {
	}

	public FileOssInfo(String id) {
		this.id = id;
	}

	public FileOssInfo(String name, String path, String length, String createTime,
		String lastUpdateTime, String id) {
		super(name, path, length, createTime, lastUpdateTime);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
