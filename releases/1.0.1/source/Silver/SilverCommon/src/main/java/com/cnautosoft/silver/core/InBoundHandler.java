package com.cnautosoft.silver.core;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.cnautosoft.h2o.common.Logger;
import com.cnautosoft.h2o.core.CommonUtil;
import com.cnautosoft.h2o.core.SystemContext;
import com.cnautosoft.h2o.web.wrapper.PaginationTO;
import com.cnautosoft.silver.entity.Config;
import com.cnautosoft.silver.service.impl.ConfigServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class InBoundHandler extends SimpleChannelInboundHandler<byte[]> {
	private static final Logger logger = Logger.getLogger(InBoundHandler.class);
	
	public static final Charset UTF_8 = Charset.forName("utf-8");
	
	private static final AttributeKey<String> CHANNEL_UUID = AttributeKey.valueOf("channel_uuid");
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		
		if(!ctx.channel().hasAttr(CHANNEL_UUID)) {
			ctx.channel().attr(CHANNEL_UUID).set(UUID.randomUUID().toString());
		}
		
		String uuid = ctx.channel().attr(CHANNEL_UUID).get();
		
		ConfigManager.getInstance().getChannelMap().put(uuid, ctx.channel());	
		logger.debug("client"+getRemoteAddress(ctx)+" connected, clientId="+uuid);
	}
 
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String uuid = ctx.channel().attr(CHANNEL_UUID).get();
		ConfigManager.getInstance().getChannelMap().remove(uuid);	
		ctx.close();
		logger.debug("client"+getRemoteAddress(ctx)+" connected, clientId="+uuid);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] msg)
			throws Exception {
		try {
			//register client to key
			String msgKey = new String(msg,UTF_8).intern();
			
			String uuid = ctx.channel().attr(CHANNEL_UUID).get();
			Set<String> watchers = ConfigManager.getInstance().getConfigWatchers().get(msgKey);
			if(watchers == null) {
				synchronized(msgKey) {
					if(watchers == null) {
						watchers = new HashSet<String>();
						ConfigManager.getInstance().getConfigWatchers().put(msgKey, watchers);
					}
				}
			}
			watchers.add(uuid);
			
			
			byte[] configBytes = ConfigManager.getInstance().getConfigCache().get(msgKey);
			if(configBytes == null) {
				synchronized(msgKey) {
					if(configBytes == null) {
						String[] msgArray = msgKey.split(",");
						if(msgArray.length!=2) {
							ctx.channel().writeAndFlush(Util.intToByte4(0));
							return;
						}
						
						//get from db
						ConfigServiceImpl configServiceImpl = (ConfigServiceImpl) SystemContext.getInstance().getManagedBean(ConfigServiceImpl.class);
						List<Config> configLs = configServiceImpl.getList(1, 10, null, PaginationTO.NOT_DELETED, msgArray[0], msgArray[1], null);
						if(configLs.size()!=1) {
							ctx.channel().writeAndFlush(Util.intToByte4(0));
							return;
						}
						
						String properties = configLs.get(0).getProperties();
						if(CommonUtil.isEmpty(properties)) {
							ctx.channel().writeAndFlush(Util.intToByte4(0));
							return;
						}
						configBytes = properties.getBytes(UTF_8);
						ConfigManager.getInstance().getConfigCache().put(msgKey, configBytes);
					}
				}
			}
			ctx.channel().writeAndFlush(Util.intToByte4(configBytes.length));
			ctx.channel().writeAndFlush(configBytes);
		}
		catch(Exception e) {
			logger.error(e);
		}
	}
	
	public static String getRemoteAddress(ChannelHandlerContext ctx){
		return ctx.channel().remoteAddress().toString();
	}
}
