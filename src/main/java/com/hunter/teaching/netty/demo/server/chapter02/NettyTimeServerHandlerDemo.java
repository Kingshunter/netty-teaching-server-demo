package com.hunter.teaching.netty.demo.server.chapter02;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyTimeServerHandlerDemo extends ChannelInboundHandlerAdapter {

    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    
    /**
     * read the client info
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf1 = (ByteBuf) msg;
        byte[] byteArr = new byte[byteBuf1.readableBytes()];
        byteBuf1.readBytes(byteArr);
        String receiveInfoWithLineSpearator = new String(byteArr, Charset.defaultCharset());
        int receiveInfoLen = receiveInfoWithLineSpearator.length() - "\r\n".length();
        String receiveInfo = receiveInfoWithLineSpearator.substring(0, receiveInfoLen);
        System.out.println("The time server receive order : " + receiveInfo);
        String currentTimeStr = "Query Time Order".equalsIgnoreCase(receiveInfo) ? String.valueOf(Instant.now().toEpochMilli()) : "Bad Order";
        ByteBuf byteBuf2 = Unpooled.copiedBuffer(currentTimeStr.getBytes());
        ctx.write(byteBuf2);
        int count = atomicInteger.incrementAndGet();
        System.out.println("server receive count = " + count);
     }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
