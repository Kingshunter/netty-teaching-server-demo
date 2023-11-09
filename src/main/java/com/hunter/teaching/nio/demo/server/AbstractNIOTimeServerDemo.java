package com.hunter.teaching.nio.demo.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractNIOTimeServerDemo {

    protected Selector selector;

    protected ServerSocketChannel serverSocketChannel;

    private String hostname;

    private int port;

    private boolean blockMode;

    private boolean multiplexMode;

    public AbstractNIOTimeServerDemo(String hostname, int port, boolean blockMode, boolean multiplexMode) {
        this.hostname = hostname;
        this.port = port;
        this.blockMode = blockMode;
        this.multiplexMode = multiplexMode;
    }

    // initial the time server
    protected void init() throws IOException {
        SocketAddress socketAddress = initSocketAddress(hostname, port);
        initServerSocketChannel(socketAddress);
        configureBlocking(blockMode);
        configureSelector(multiplexMode);
    }

    private SocketAddress initSocketAddress(String hostname, int port) {
        SocketAddress socketAddress = new InetSocketAddress(hostname, port);
        return socketAddress;
    }

    private void initServerSocketChannel(SocketAddress socketAddress) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(socketAddress, 1024);
    }

    private void configureBlocking(boolean blockMode) throws IOException {
        serverSocketChannel.configureBlocking(blockMode);
    }

    private void configureSelector(boolean multiplexMode) throws IOException {
        if (multiplexMode) {
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
    }
    
    protected void listen() {
        Set<SocketChannel> socketChannelList = new HashSet<SocketChannel>();
        while (true) {
            try {
                SocketChannel sc = serverSocketChannel.accept();
                sc.configureBlocking(blockMode);
                if (sc != null) {
                    socketChannelList.add(sc);
                }
                Iterator<SocketChannel> iterator = socketChannelList.iterator();
                while (iterator.hasNext()) {
                    SocketChannel socketChannel = iterator.next();
                    System.out.println(socketChannel.finishConnect());
                    System.out.println(socketChannel.isConnected());
                    System.out.println(socketChannel.isOpen());
                        Socket socket = socketChannel.socket();
                        String clientIp = socket.getInetAddress().getHostName();
                        int clientPort = socket.getPort();
                        System.out.println("clientIp=" + clientIp + ",clientPort=" + clientPort + " : " + socket.isClosed()); 
                        System.out.println("clientIp=" + clientIp + ",clientPort=" + clientPort + " is connected");
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int readBytes = socketChannel.read(byteBuffer);
                        if (readBytes > 0) {
                            byte[] byteArr = new byte[readBytes];
                            byteBuffer.flip();
                            byteBuffer.get(byteArr);
                            String receiveInfo = new String(byteArr);
                            System.out.println("The time server receive order : " + receiveInfo);
                            if ("Query Time Order".equals(receiveInfo)) {
                                byteBuffer.clear();
                                ByteBuffer buff = StandardCharsets.UTF_8.encode(String.valueOf(Instant.now().toEpochMilli()));
                                socketChannel.write(buff);
                            } else {
                                ByteBuffer buff = StandardCharsets.UTF_8.encode("Bad Order");
                                socketChannel.write(buff);
                            }
                    } else {
                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
