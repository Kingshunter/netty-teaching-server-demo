package com.hunter.teaching.nio.demo.server.chapter02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NIOTimeNonBlockingServerDemo {
    
    private static final int PORT = 6666;
    
    private static int LOOP = 100;

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        SocketAddress socketAddress = new InetSocketAddress(PORT);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(socketAddress, 5);
        System.out.println("server is ok!");
        Set<SocketChannel> socketChannels = new HashSet<SocketChannel>();
        do {
            LOOP--;
            SocketChannel sc = serverSocketChannel.accept();
            if (sc != null) {
                sc.configureBlocking(false);
                socketChannels.add(sc);
            }
            System.out.println(socketChannels.size());
            Iterator<SocketChannel> iterator = socketChannels.iterator();
            while (iterator.hasNext()) {
                SocketChannel socketChannel = iterator.next();
                if (socketChannel.isConnected()) {
                    Socket socket = socketChannel.socket();
                    String clientIp = socket.getInetAddress().getHostAddress();
                    int clientPort = socket.getPort();
                    System.out.println("clientIp=" + clientIp + ",clientPort=" + clientPort + " is connected");
                    try {
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
                    } catch (Exception e) {
                        socketChannel.close();
                        iterator.remove();
                        e.printStackTrace();
                    }
                }
            }
            TimeUnit.SECONDS.sleep(3);
        } while (LOOP > 0);
        serverSocketChannel.close();
    }

}
