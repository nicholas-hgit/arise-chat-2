package com.arise.chat.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {

    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);

    public static void main(String[] args) {

        try(SocketChannel sChannel = SocketChannel.open();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in,StandardCharsets.UTF_8));
            ExecutorService executor = Executors.newSingleThreadExecutor()){

            logger.info("CONNECTING TO SERVER...");
            sChannel.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(),8080));

            executor.execute(new MessageListener(sChannel));

            while (sChannel.isConnected()){

                String message = reader.readLine();
                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
                sChannel.write(buffer);
            }

        } catch (IOException e) {
             logger.error("CLIENT STOPPED RUNNING",e);
        }
    }
}
