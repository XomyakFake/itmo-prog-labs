package ru.itmo.server.modules;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Server {
    private InetSocketAddress address;
    private Logger logger = LoggerFactory.getLogger(Server.class);;

    public Server(InetSocketAddress address){
        this.address = address;
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
            

            ByteBuffer buffer = ByteBuffer.allocate(2048); //сервер выделяет размер но такого не должно быть


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

                            channel.send(ByteBuffer.wrap(responseData), clientAddress); 

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





//
//декомпозиция сервера
//разбить на пакеты тоесть получать и отдавать байты если много 