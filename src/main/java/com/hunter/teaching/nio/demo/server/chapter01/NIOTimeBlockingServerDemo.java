package com.hunter.teaching.nio.demo.server.chapter01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.Instant;


public class NIOTimeBlockingServerDemo {

    private static final String HOST_NAME = "127.0.0.1";

    private static final int PORT = 6666;
    
    private static int LOOP = 100;

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(HOST_NAME, PORT);
        serverSocketChannel.bind(socketAddress);
        System.out.println("server is ok!");
        do {
            LOOP--;
            SocketChannel socketChannel = serverSocketChannel.accept();
            Socket socket = socketChannel.socket();
            String clientIp = socket.getInetAddress().getHostAddress();
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
                    ByteBuffer buff = StandardCharsets.UTF_8.encode(String.valueOf(Instant.now().toEpochMilli()));
                    socketChannel.write(buff);
                } else {
                    ByteBuffer buff = StandardCharsets.UTF_8.encode("Bad Order");
                    socketChannel.write(buff);
                }
            }
            socketChannel.close();
        } while (LOOP > 0);
        serverSocketChannel.close();
    }

}
