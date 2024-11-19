package com.arise.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

public class MessageHandler implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private final SocketChannel sChannel;
    private final List<SocketChannel> channels;

    MessageHandler(SocketChannel sChannel, List<SocketChannel> channels){
        this.sChannel = sChannel;
        this.channels = channels;
    }

    private void broadcastMessage(ByteBuffer buffer) throws IOException {

        for(SocketChannel channel : channels){

            channel.write(buffer);
            buffer.rewind();
        }
    }
    @Override
    public void run() {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {

            sChannel.read(buffer);
            buffer.flip();

            broadcastMessage(buffer);

        } catch (IOException e) {
             logger.error("COULD NOT READ FROM CHANNEL",e);
        }

    }


}
