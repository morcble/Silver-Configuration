package com.cnautosoft.silver.client;

import java.nio.charset.Charset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class InBoundHandler extends SimpleChannelInboundHandler<byte[]> {
	private static final Logger logger = LogManager.getLogger(InBoundHandler.class);
	
	public static final Charset UTF_8 = Charset.forName("utf-8");
	
	private SilverClient silverClient;
	public InBoundHandler(SilverClient silverClient) {
		this.silverClient = silverClient;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		ctx.channel().writeAndFlush(silverClient.getGroupId()+","+silverClient.getDataId());
		logger.debug("Silver Server connected");
	}
 
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.close();
		logger.debug("Silver Server disconnected");
		silverClient.reconnect();
	}
	
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] msg)
			throws Exception {
		try {
			ByteBuf receiveCache = silverClient.getReceiveCache();
			if(silverClient.getMsgLength()==-1) {
				receiveCache.writeBytes(msg);
				
				byte[] lengthBytes = new byte[4];
				receiveCache.readBytes(lengthBytes, 0, 4);
				
				int length = Util.byte4ToInt(lengthBytes, 0);
				if(length==0) {
					logger.warn("Properties not found in Silver Server");
					silverClient.notifyListener("");
					resetCache();
				}
				else {
					silverClient.setMsgLength(length);
					if(silverClient.getMsgLength()==receiveCache.readableBytes()) {
						silverClient.notifyListener(receiveCache.toString(UTF_8));
						resetCache();
					}
				}
			}
			else {
				receiveCache.writeBytes(msg);
				if(silverClient.getMsgLength()==receiveCache.readableBytes()) {
					silverClient.notifyListener(receiveCache.toString(UTF_8));
					resetCache();
				}
			}
		}
		catch(Exception e) {
			logger.debug(e);
		}
	}
	
	private void resetCache() {
		silverClient.getReceiveCache().clear();
		silverClient.setMsgLength(-1);
	}
}
