package ru.itmo.server.modules;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Server {
    private InetSocketAddress address;
    private Logger logger = LoggerFactory.getLogger(Server.class);;
    private static final int PACKET_SIZE = 60000;
    private static final int DATA_SIZE = PACKET_SIZE - 1; 

    public Server(InetSocketAddress address){
        this.address = address;
    }

    private void sendChunks(DatagramChannel channel, byte[] responseData, SocketAddress clientAddress) throws IOException{
        int n = (int) Math.ceil((double) responseData.length / 60000);
        for(int i = 0; i < n; i++){
            int from = i * DATA_SIZE;
            int to = Math.min((int)(from + DATA_SIZE), (int)(responseData.length)) ;
            byte[] chunk = Arrays.copyOfRange(responseData, from, to);
            byte[] packet = new byte[chunk.length + 1];
            System.arraycopy(chunk, 0, packet, 0, chunk.length);
            if(i == n-1){
                packet[packet.length-1] = 1;
            }
            else{
                packet[packet.length-1] = 0;
            }
            channel.send(ByteBuffer.wrap(packet), clientAddress);

        }

    }

    public void run(){
        CommandInvoker commandinvoker = new CommandInvoker();
        DumpManager dumpManager = new DumpManager();
        CollectionManager collectionmanager = new CollectionManager(dumpManager);
        collectionmanager.loadCollection();

        CommandRegistr.register(commandinvoker, collectionmanager);

        StorageCommands storage = new StorageCommands();
        RequestHandler requestHandler = new RequestHandler(commandinvoker, storage);

        logger.info("Сервер начал работу");
        logger.info("Сервер запущен на {}", address);

        try (Scanner scanner = new Scanner(System.in);
            DatagramChannel channel = DatagramChannel.open()) {
            Selector selector = Selector.open();
            channel.configureBlocking(false);
            channel.bind(address);
            channel.register(selector, SelectionKey.OP_READ);
            

<<<<<<< HEAD
            ByteBuffer buffer = ByteBuffer.allocate(65507); 
=======
            ByteBuffer buffer = ByteBuffer.allocate(65507);


>>>>>>> ad9ba44 (Update)
            while(true){
                if(System.in.available() > 0){
                    String command = scanner.nextLine();
                    if(command.equals("save")){
                        collectionmanager.saveCollection();
                    }
                    if(command.equals("exit")){
                        collectionmanager.saveCollection();
                        logger.info("Сервер завершил работу");
                        System.exit(0);
                    }
                }

                selector.select(500);
                Set<SelectionKey> keys = selector.selectedKeys();
                for(var iter = keys.iterator(); iter.hasNext(); ){
                    SelectionKey key = iter.next();
                    iter.remove();
                    if(key.isValid()){
                        if(key.isReadable()){
                            buffer.clear();
                            SocketAddress clientAddress = channel.receive(buffer);
                            buffer.flip();

                            byte[] data = new byte[buffer.limit()];
                            buffer.get(data);

                            byte[] responseData = requestHandler.handle(data, clientAddress.toString());
                            sendChunks(channel, responseData, clientAddress);

                        }
                    }
                }

            }
            
        }
        catch(Exception e){
            logger.error("Ошибка сервера", e);
        }
    }
}


<<<<<<< HEAD

=======
>>>>>>> ad9ba44 (Update)
