package com.arise.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("InfiniteLoopStatement")
public class ChatServer {

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
    private static final List<SocketChannel> clients = new ArrayList<>();

    public static void main(String[] args){

        logger.info("STARTING SERVER...");

        try(ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ExecutorService executor = Executors.newCachedThreadPool();
            Selector selector = Selector.open()){

            ssChannel.configureBlocking(false);
            ssChannel.bind(new InetSocketAddress(8080));
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("SERVER STARTED ON PORT: 8080");

            while(true){

                int readyChannels = selector.select();

                if(readyChannels > 0){

                    for(SelectionKey key : selector.selectedKeys()){

                        if(key.isAcceptable()){

                            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                            SocketChannel conn = channel.accept();

                            writeServerName(conn);
                            promptUsername(conn);

                            conn.configureBlocking(false);
                            conn.register(selector, SelectionKey.OP_READ);
                            clients.add(conn);

                        }else if(key.isReadable()){

                            SocketChannel sChannel = (SocketChannel) key.channel();
                            executor.execute(new MessageHandler(sChannel,clients));
                        }
                    }

                    selector.selectedKeys().clear();
                }
            }

        } catch (IOException e) {
             logger.error("SERVER STOPPED RUNNING",e);
        }
    }


    private static String serverName(){

        return """
                   _____        .__              \s
                  /  _  \\_______|__| ______ ____ \s
                 /  /_\\  \\_  __ \\  |/  ___// __ \\\s
                /    |    \\  | \\/  |\\___ \\\\  ___/\s
                \\____|__  /__|  |__/____  >\\___  >
                        \\/              \\/     \\/\s
                """;
    }

    private static void writeServerName(SocketChannel sChannel) throws IOException {

        sChannel.write(ByteBuffer.wrap(serverName().getBytes(StandardCharsets.UTF_8)));
    }

    private static void promptUsername(SocketChannel sChannel) throws IOException {

        String prompt = "Enter username";
        sChannel.write(ByteBuffer.wrap(prompt.getBytes(StandardCharsets.UTF_8)));
    }

}
