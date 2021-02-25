/*
 * Copyright 2002-2021 the original author or authors.
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
package com.taotao.cloud.ws.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * NettyByteEncoder
 *
 * @author dengtao
 * @since 2020/12/30 下午4:44
 * @version 1.0.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyByteEncoder extends MessageToByteEncoder<byte[]> {
	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, byte[] bytes, ByteBuf out) throws Exception {
		log.info("NettyByteEncoder 被调用");
		//字节数组
		out.writeBytes(bytes);
	}
}
