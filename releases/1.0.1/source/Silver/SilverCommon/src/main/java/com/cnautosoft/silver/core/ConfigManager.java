package com.cnautosoft.silver.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.cnautosoft.h2o.common.Logger;
import com.cnautosoft.h2o.core.CommonUtil;
import com.cnautosoft.h2o.properties.ConfigUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * @author morcble
 */
public class ConfigManager {
	private static final Logger logger = Logger.getLogger(ConfigManager.class);
	
	private static ConfigManager instance;
	private ConfigManager() {}

	public static ConfigManager getInstance(){
		if(instance==null) {
			synchronized(ConfigManager.class){
				if(instance==null) {
					instance = new ConfigManager();
					
					String portStr = ConfigUtil.getProperty("silver.port");
					if(CommonUtil.isEmpty(portStr)) {
						portStr = "8081";
					}
					instance.start(Integer.parseInt(portStr));
				}
			}
		}
		return instance;
	}
	

	
	
	/**
	 * 存储config properties
	 */
	private Map<String, byte[]> configCache = new ConcurrentHashMap<String, byte[]>();
	
	private Map<String, Set<String>> configWatchers = new ConcurrentHashMap<String, Set<String>>();
	
	private Map<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();
	
	
	private void start(int port){
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel ch) throws Exception {
                                	ch.pipeline().addLast(new ByteArrayDecoder());
                                	ch.pipeline().addLast(new ByteArrayEncoder());                        	
                                	ch.pipeline().addLast(new OutBoundHandler());
                                    ch.pipeline().addLast(new InBoundHandler());
                                }
                            })
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_RCVBUF, 256)
    	            .option(ChannelOption.SO_SNDBUF, 256)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            		
            b.bind(port).sync();
            logger.debug("Silver Server listening on "+port+" ...");
            //f.channel().closeFuture().sync();
        } 
        catch(InterruptedException e) {
        	logger.error(e);
        }
        
        Runtime.getRuntime().addShutdownHook(
        	new Thread(() -> {
        		bossGroup.shutdownGracefully();
        		workerGroup.shutdownGracefully();
        	}
        ));
	}

	public Map<String, Channel> getChannelMap() {
		return channelMap;
	}
	
	public Map<String, byte[]> getConfigCache() {
		return configCache;
	}

	public Map<String, Set<String>> getConfigWatchers() {
		return configWatchers;
	}
}
