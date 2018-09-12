package com.cnautosoft.silver.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutBoundHandler extends ChannelOutboundHandlerAdapter {

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		
		if (msg instanceof byte[]) {
			byte[] bytesWrite = (byte[])msg;
			ByteBuf buf = ctx.alloc().buffer(bytesWrite.length); 
			buf.writeBytes(bytesWrite); 
			ctx.writeAndFlush(buf);
		}
	}
}
