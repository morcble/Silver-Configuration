package com.cnautosoft.server;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;
import javax.activation.MimetypesFileTypeMap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

    private final String context;
    private final String baseDoc;
    
    public HttpFileServerHandler(String context,String baseDoc) {
        this.context = context;
        this.baseDoc = baseDoc;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx,FullHttpRequest request) throws Exception {
        if(!request.decoderResult().isSuccess()){
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if(request.method() != HttpMethod.GET){
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        
        String uri = request.uri();
        int index  = uri.indexOf("?");
        if(index!=-1) {
        	uri = uri.substring(0, index);
        }
        String path = sanitizeUri(uri);
 
        if(path == null){
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        
        File file = new File(path);
        if(file.isDirectory()) {
        	sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        
        if(file.isHidden() || !file.exists()){
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        
        if(!file.isFile()){
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        
        RandomAccessFile randomAccessFile = null;
        try{
            randomAccessFile = new RandomAccessFile(file, "r");
        }catch(FileNotFoundException fnfd){
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        
        
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,fileLength);
        setContentTypeHeader(response, file);
        
        if(HttpUtil.isKeepAlive(request)){
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        
        ctx.write(response);
        
        RandomAccessFile tmp = randomAccessFile;
        ChannelFuture sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            
            @Override
            public void operationComplete(ChannelProgressiveFuture future)
                    throws Exception {
            	tmp.close();  
            }
            
            @Override
            public void operationProgressed(ChannelProgressiveFuture future,
                    long progress, long total) throws Exception {
                
            }
        });
        
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if(!HttpUtil.isKeepAlive(request))
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if(ctx.channel().isActive()) sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }
    
    private static final String UTF_8 = "UTF-8";
    private String sanitizeUri(String uri){
        try{
            uri = URLDecoder.decode(uri, UTF_8);
        }catch(UnsupportedEncodingException e){
            try{
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            }catch(UnsupportedEncodingException e1){
                throw new Error();
            }
        }
        
        if(!uri.startsWith(context)) {
        	uri = "index.html";
        }
        else {
        	if(!uri.startsWith("/"))
             	uri = "index.html";
        	else {
        		uri = uri.replaceFirst(context, "");
                uri = uri.replace('/', File.separatorChar);
        	}
        }
        return baseDoc + File.separator + uri;
    }
    
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    
 
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, 
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    private static void setContentTypeHeader(HttpResponse response, File file){
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
  
        if(file.getPath().endsWith(".css")) {
        	response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/css");
        }
        else {
        	response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file.getPath()));
        }
    }

}