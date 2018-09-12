package com.cnautosoft.server;

import java.net.InetAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class SimpleHtmlServer {
    
    public void run(final int port, final String context,final String baseDoc)throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new HttpRequestDecoder());
                    ch.pipeline().addLast(new HttpObjectAggregator(65536));
                    ch.pipeline().addLast(new HttpResponseEncoder());
                    ch.pipeline().addLast(new ChunkedWriteHandler());
                    ch.pipeline().addLast(new HttpFileServerHandler(context,baseDoc));
                }
            });
            
            ChannelFuture f = b.bind(port).sync();
            System.out.println("Silver前端服务地址： " + "http://"+InetAddress.getLocalHost().getHostAddress()+":" + port + context+"/index.html");
        }finally{
        	Runtime.getRuntime().addShutdownHook(
                	new Thread(() -> {
                		bossGroup.shutdownGracefully();
                		workerGroup.shutdownGracefully();
                	}
                ));
        }
    }
    
    public static void main(String[] args)throws Exception {
    	String htmlContext = "/SilverFrontend";
        String baseDoc = "/home/fenglj/devspace/myprojects/Silver/dist/SilverFrontend";
        
    	
    	
        new SimpleHtmlServer().run(8888, htmlContext,baseDoc);
    }
}