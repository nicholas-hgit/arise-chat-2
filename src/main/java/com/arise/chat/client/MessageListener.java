package com.arise.chat.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class MessageListener implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private final SocketChannel sChannel;

    MessageListener(SocketChannel sChannel){
        this.sChannel = sChannel;
    }
    @Override
    public void run() {

        try(Selector selector = Selector.open()) {

            sChannel.configureBlocking(false);
            sChannel.register(selector,SelectionKey.OP_READ);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuffer message = new StringBuffer();

            while (sChannel.isConnected()){

                int readyChannels = selector.select();

                if(readyChannels > 0){

                    sChannel.read(buffer);
                    buffer.flip();

                    CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                    buffer.clear();

                    while (charBuffer.hasRemaining()){
                        message.append(charBuffer.get());
                    }

                    System.out.println(message);
                    message.setLength(0);
                }

                selector.selectedKeys().clear();
            }
        } catch (IOException e) {
            logger.error("MESSAGE LISTENER STOPPED LISTENING TO MESSAGES",e);
        }

    }
}
