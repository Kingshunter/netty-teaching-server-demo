package com.hunter.teaching.nio.demo.server.chapter03;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class NIOTimeMultiplexServerDemo {

    private static final int PORT = 6666;

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(PORT);
        Selector selector = Selector.open();

    }

}
