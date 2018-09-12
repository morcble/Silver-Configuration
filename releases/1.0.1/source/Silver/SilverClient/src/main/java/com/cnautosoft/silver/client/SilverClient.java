package com.cnautosoft.silver.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class SilverClient {
	private static final Logger logger = LogManager.getLogger(SilverClient.class);
	
	private String host;
	private int port;
	private String groupId;
	private String dataId;
	
	/**
	 * receive cache 
	 */
	private ByteBuf receiveCache = PooledByteBufAllocator.DEFAULT.buffer();
	private int msgLength = -1;
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	public SilverClient(String host, int port, String groupId, String dataId) throws Exception {
		super();
		this.host = host;
		this.port = port;
		this.groupId = groupId;
		this.dataId = dataId;
		
		try {
			connectSilverServer();
		} catch (Exception e) {
			throw e;
		}
	}
	

	public String getGroupId() {
		return groupId;
	}


	public int getMsgLength() {
		return msgLength;
	}


	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}


	public ByteBuf getReceiveCache() {
		return receiveCache;
	}


	public String getDataId() {
		return dataId;
	}

	private EventLoopGroup group = new NioEventLoopGroup();
	private void connectSilverServer() throws Exception{
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class)
		        .handler(new ChannelInitializer<SocketChannel>() {
		            @Override
		            public void initChannel(SocketChannel ch) throws Exception {
		                ch.pipeline().addLast(new ByteArrayDecoder());
                    	ch.pipeline().addLast(new ByteArrayEncoder());                        	
                    	ch.pipeline().addLast(new OutBoundHandler());
                        ch.pipeline().addLast(new InBoundHandler(SilverClient.this));
		            }
		        })
	            .option(ChannelOption.SO_RCVBUF, 256)
	            .option(ChannelOption.SO_SNDBUF, 256)
	            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
	            .option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.connect(host, port).sync(); 
		}
		catch(Exception e) {
			logger.error(e);
		}
		
		Runtime.getRuntime().addShutdownHook(
	        	new Thread(() -> {
	        		group.shutdownGracefully();
	        	}
	    ));
	}
	
	private boolean connected;
	public void reconnect() {
		connected = false;
		while(!connected) {
			logger.debug("reconnecting...");
			try {
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(group).channel(NioSocketChannel.class)
			        .handler(new ChannelInitializer<SocketChannel>() {
			            @Override
			            public void initChannel(SocketChannel ch) throws Exception {
			                ch.pipeline().addLast(new ByteArrayDecoder());
	                    	ch.pipeline().addLast(new ByteArrayEncoder());                        	
	                    	ch.pipeline().addLast(new OutBoundHandler());
	                        ch.pipeline().addLast(new InBoundHandler(SilverClient.this));
			            }
			        })
		            .option(ChannelOption.SO_RCVBUF, 256)
		            .option(ChannelOption.SO_SNDBUF, 256)
		            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
		            .option(ChannelOption.SO_KEEPALIVE, true);
				bootstrap.connect(host, port).sync(); 
				logger.debug("connected");
				connected = true;
			}
			catch(Exception e) {
				logger.debug("connect failed...");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}


	private List<PropertyChangedListener> propertyChangedListeners = new ArrayList<PropertyChangedListener>();
	
	public void addPropertyChangedListener(PropertyChangedListener propertyChangeListener) {
		propertyChangedListeners.add(propertyChangeListener);
		if(latch!=null) {
			try {
				latch.await();
				latch = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	public void notifyListener(String properties) {
		for(PropertyChangedListener propertyListener:propertyChangedListeners) {
			propertyListener.onChanged(properties);
		}
		if(latch!=null)latch.countDown();
	}
}
