package com.hunter.teaching.nio.demo.server.chapter06;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

public class NIOTimeServerDemo {

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    private static final String HOST_NAME = "127.0.0.1";

    private static final int PORT = 6666;

    public NIOTimeServerDemo() {
        init();
    }

    // initial the time
    private void init() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress(HOST_NAME, PORT);
            serverSocketChannel.bind(socketAddress, 1024);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void listen() {
        while (true) {
            try {
                int cnt = selector.select();
                if (cnt == 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                if (CollectionUtils.isNotEmpty(selectionKeySet)) {
                    Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isValid()) {
                            if (selectionKey.isAcceptable()) {
                                SocketChannel socketChannel = serverSocketChannel.accept();
                                InetAddress inetAddress = socketChannel.socket().getLocalAddress();
                                System.out.println(inetAddress.getHostName() );
                                socketChannel.configureBlocking(false);
                                socketChannel.register(selector, cnt);
                                socketChannel.close();
                            }
                            else if (selectionKey.isReadable()) {
                                readData(selectionKey);
                            }
                            iterator.remove();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
    }

    private void readData(SelectionKey selectionKey) {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            int len = socketChannel.read(byteBuffer);
            if (len > 0) {
                byte[] array = new byte[len];
                String info = new String(array);
                System.out.println(info);
            }
            
        } catch (IOException e) {
            
            try {
                socketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            
                selectionKey.cancel();
            
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        NIOTimeServerDemo nioTimeServerDemo = new NIOTimeServerDemo();
        nioTimeServerDemo.listen();
    }

}
