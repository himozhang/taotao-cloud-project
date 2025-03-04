// Copyright 2022 The Ip2Region Authors. All rights reserved.
// Use of this source code is governed by a Apache2.0-style
// license that can be found in the LICENSE file.
// @Author Lion <chenxin619315@gmail.com>
// @Date   2022/06/23

package com.taotao.cloud.ip2region.model;


/**
 * 头
 *
 * @author shuigedeng
 * @version 2022.06
 * @since 2022-06-28 17:42:08
 */
public class Header {

	public final int version;
	public final int indexPolicy;
	public final int createdAt;
	public final int startIndexPtr;
	public final int endIndexPtr;

	public Header(byte[] buff) {
		assert buff.length >= 16;
		version = Searcher.getInt2(buff, 0);
		indexPolicy = Searcher.getInt2(buff, 2);
		createdAt = Searcher.getInt(buff, 4);
		startIndexPtr = Searcher.getInt(buff, 8);
		endIndexPtr = Searcher.getInt(buff, 12);
	}

	@Override
	public String toString() {
		return "{" +
			"Version: " + version + ',' +
			"IndexPolicy" + indexPolicy + ',' +
			"CreatedAt" + createdAt + ',' +
			"StartIndexPtr" + startIndexPtr + ',' +
			"EndIndexPtr" + endIndexPtr +
			'}';
	}
}
