package com.hunter.teaching.netty.demo.server.chapter03.part03;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * This Demo consider the tcp stick package or tcp unpacking issues
 * 
 */
public class NettyTimeServerDemo {

    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new NettyChildChannelHandlerServerDemo());
            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } catch (Exception e) {
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class NettyChildChannelHandlerServerDemo extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel sc) throws Exception {
            ByteBuf delimiterBuf = Unpooled.copiedBuffer("&".getBytes());
            // DelimiterBasedFrameDecoder slove the tcp stick package or tcp unpacking issues
            sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiterBuf));
            // StringDecoder transfer the bytebuf object to string object automatically
            sc.pipeline().addLast(new StringDecoder());
            sc.pipeline().addLast(new NettyTimeServerHandlerDemo());
        }

    }
    
    public static void main(String[] args) throws Exception {
        NettyTimeServerDemo nettyTimeServerDemo = new NettyTimeServerDemo();
        nettyTimeServerDemo.bind(10080);
    }

}
