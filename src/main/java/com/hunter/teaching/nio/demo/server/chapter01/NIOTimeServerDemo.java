package com.hunter.teaching.nio.demo.server.chapter01;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hunter.teaching.nio.demo.server.AbstractNIOTimeServerDemo;

public class NIOTimeServerDemo extends AbstractNIOTimeServerDemo {
    
    public NIOTimeServerDemo() throws Exception {
        init();
    }

    private void listen() {
        List<SocketChannel> socketChannelList = new ArrayList<SocketChannel>();
        while (true) {
            try {
                SocketChannel sc = serverSocketChannel.accept();
                if (sc != null) {
                    socketChannelList.add(sc);
                }
                Iterator<SocketChannel> iterator = socketChannelList.iterator();
                while (iterator.hasNext()) {
                    SocketChannel socketChannel = iterator.next();
                    if (socketChannel.isConnected()) {
                        Socket socket = socketChannel.socket();
                        String clientIp = socket.getInetAddress().getHostName();
                        int clientPort = socket.getPort();
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
                                String currentTimeStr = String.valueOf(Instant.now().toEpochMilli());
                                ByteBuffer buff = StandardCharsets.UTF_8.encode(currentTimeStr);
                                socketChannel.write(buff);
                            } else {
                                ByteBuffer buff = StandardCharsets.UTF_8.encode("Bad Order");
                                socketChannel.write(buff);
                            }
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

    public static void main(String[] args) {
        try {
            NIOTimeServerDemo nioTimeServerDemo = new NIOTimeServerDemo();
            nioTimeServerDemo.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void selector() {
    }

    @Override
    protected void configureBlocking() {
    }

}
