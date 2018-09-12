package com.cnautosoft.silver.client;


import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutBoundHandler extends ChannelOutboundHandlerAdapter {
	public static final Charset UTF_8 = Charset.forName("utf-8");
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		if (msg instanceof String) {
			byte[] bytesWrite = ((String)msg).getBytes(UTF_8);
			ByteBuf buf = ctx.alloc().buffer(bytesWrite.length); 
			buf.writeBytes(bytesWrite); 
			ctx.writeAndFlush(buf);
			buf.release();
		}
	}
}
