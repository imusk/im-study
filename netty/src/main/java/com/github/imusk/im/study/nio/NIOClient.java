package com.github.imusk.im.study.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Musk
 * @date 2020-07-20 15:56
 * @email muskcool@protonmail.com
 * @description JDK原生的NIO实现客户端
 * 一、使用 NIO 完成网络通信的三个核心：
 *
 * 1. 通道（Channel）：负责连接
 *
 * 	   java.nio.channels.Channel 接口：
 * 			|--SelectableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 *
 * 				|--Pipe.SinkChannel
 * 				|--Pipe.SourceChannel
 *
 * 2. 缓冲区（Buffer）：负责数据的存取
 *
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况
 *
 */
public class NIOClient {

    public static void aa(String arg[]) {
        System.out.println("客户端已经启动.............");
        try {
            //1、创建socker通道
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));
            //2、切换异步非阻塞
            socketChannel.configureBlocking(false); //1.7及以上
            //3、指定缓冲区大小
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put(new Date().toString().getBytes());
            //4、切换到读取模式
            byteBuffer.flip();
            //5、写入到缓冲区
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            //6、关闭通道
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        //1、创建socker通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));

        //2、切换异步非阻塞
        socketChannel.configureBlocking(false); //1.7及以上

        new Thread(() -> {

            try {

                while (true) {
                    try {
                        //3、指定缓冲区大小
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        byteBuffer.put((new Date() + " : Hello World~").getBytes());
                        //4、切换到读取模式
                        byteBuffer.flip();
                        //5、写入到缓冲区
                        socketChannel.write(byteBuffer);
                        byteBuffer.clear();
                        System.out.println(new Date() + " : 发送成功，等待 2s 下次发送");
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                //6、关闭通道
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();

    }

    public static void bb(String[] args) throws Exception {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress("localhost", 8888));

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    if (selectionKey.isConnectable()) {
                        SocketChannel client = (SocketChannel) selectionKey.channel();

                        if (client.isConnectionPending()) {
                            client.finishConnect();

                            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                            writeBuffer.put((LocalDateTime.now() + " 连接成功").getBytes());
                            writeBuffer.flip();
                            client.write(writeBuffer);

                            ExecutorService executorService = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
                            executorService.submit(() -> {
                                while (true) {
                                    try {
                                        writeBuffer.clear();
                                        InputStreamReader input = new InputStreamReader(System.in);
                                        BufferedReader br = new BufferedReader(input);

                                        String sendMsg = br.readLine();
                                        writeBuffer.put(sendMsg.getBytes());
                                        writeBuffer.flip();
                                        client.write(writeBuffer);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        client.register(selector, SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {
                        SocketChannel client = (SocketChannel) selectionKey.channel();

                        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                        int count = client.read(readBuffer);
                        if (count > 0) {
                            String receivedMsg = new String(readBuffer.array(), 0, count);
                            System.out.println(receivedMsg);
                        }
                    }
                }
                selectionKeys.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
