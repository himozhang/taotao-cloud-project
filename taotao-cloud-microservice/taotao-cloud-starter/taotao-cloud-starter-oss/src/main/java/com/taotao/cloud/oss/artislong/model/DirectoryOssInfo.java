package com.taotao.cloud.oss.artislong.model;


import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹信息对象
 *
 * @author 陈敏
 * @version DirectoryInfo.java, v 1.1 2021/11/15 10:21 chenmin Exp $ Created on 2021/11/15
 */
public class DirectoryOssInfo extends OssInfo {

	/**
	 * 文件夹列表
	 */
	private List<FileOssInfo> fileInfos = new ArrayList<>();

	/**
	 * 文件列表
	 */
	private List<DirectoryOssInfo> directoryInfos = new ArrayList<>();

	public DirectoryOssInfo() {
	}

	public DirectoryOssInfo(List<FileOssInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}

	public DirectoryOssInfo(String name, String path, String length, String createTime,
		String lastUpdateTime, List<FileOssInfo> fileInfos) {
		super(name, path, length, createTime, lastUpdateTime);
		this.fileInfos = fileInfos;
	}

	public List<FileOssInfo> getFileInfos() {
		return fileInfos;
	}

	public void setFileInfos(List<FileOssInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}

	public List<DirectoryOssInfo> getDirectoryInfos() {
		return directoryInfos;
	}

	public void setDirectoryInfos(
		List<DirectoryOssInfo> directoryInfos) {
		this.directoryInfos = directoryInfos;
	}
}
