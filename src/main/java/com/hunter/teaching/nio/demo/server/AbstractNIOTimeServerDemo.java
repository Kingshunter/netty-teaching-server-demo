package com.hunter.teaching.nio.demo.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public abstract class AbstractNIOTimeServerDemo {

    protected Selector selector;

    protected ServerSocketChannel serverSocketChannel;

    private static final String HOST_NAME = "127.0.0.1";

    private static final int PORT = 6666;

    // initial the time server
    protected void init() throws IOException {
        selector();
        serverSocketChannel = ServerSocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(HOST_NAME, PORT);
        serverSocketChannel.bind(socketAddress, 1024);
        configureBlocking();
        register();
    }

    protected abstract void selector();

    protected abstract void configureBlocking();

    protected void register() throws ClosedChannelException {
        if (selector != null) {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
    }

}
