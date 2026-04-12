package ru.itmo.server.modules;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Logger;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.commands.*;

public class Server {
    private InetSocketAddress address;
    private Logger logger = LoggerFactory.getLogger(Server.class);;
    private Request request;
    private Response response;
    private Map<String, byte[]> reqIds = new LinkedHashMap<>();
    private final int maxSize = 100;

    public Server(InetSocketAddress address){
        this.address = address;
    }

    public void run(){
        CommandInvoker commandinvoker = new CommandInvoker();
        DumpManager dumpManager = new DumpManager();
        CollectionManager collectionmanager = new CollectionManager(dumpManager);
        collectionmanager.loadCollection();

        commandinvoker.register(new Help(commandinvoker));
        commandinvoker.register(new Info(collectionmanager));
        commandinvoker.register(new Show(collectionmanager));
        commandinvoker.register(new Clear(collectionmanager));
        commandinvoker.register(new Exit(collectionmanager));
        commandinvoker.register(new Add(collectionmanager));
        commandinvoker.register(new RemoveById(collectionmanager));
        commandinvoker.register(new History(commandinvoker));
        commandinvoker.register(new FilterGreaterThanMpaaRating(collectionmanager));
        commandinvoker.register(new PrintDescending(collectionmanager));
        commandinvoker.register(new PrintFieldDescendingTagline(collectionmanager));
        commandinvoker.register(new AddIfMax(collectionmanager));
        commandinvoker.register(new RemoveGreater(collectionmanager));
        commandinvoker.register(new Update(collectionmanager));
        commandinvoker.register(new Save(collectionmanager));

        logger.info("Сервер начал работу");
        logger.info("Сервер запущен на {}", address);

        try (Scanner scanner = new Scanner(System.in);
            DatagramChannel channel = DatagramChannel.open()) {
            Selector selector = Selector.open();
            channel.configureBlocking(false);
            channel.bind(address);
            channel.register(selector, SelectionKey.OP_READ);
            

            ByteBuffer buffer = ByteBuffer.allocate(2048);

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

                            try{
                                ObjectInputStream clientData = new ObjectInputStream(new ByteArrayInputStream(data));
                                request = (Request) clientData.readObject();

                                if(reqIds.containsKey(request.getRequestId().toString())){
                                    logger.info("Повторная отправка ответа");
                                    channel.send(ByteBuffer.wrap(reqIds.get(request.getRequestId().toString())), clientAddress);
                                }
                                else{                                
                                    MDC.put("requestId", request.getRequestId().toString());

                                    logger.info("Команда получена {} от {}", request.getCommandName(), clientAddress);
                                    

                                    response = commandinvoker.execute(request);
                                    response.setResponseId(request.getRequestId());
                                    logger.info("Комманда {} выполнена", request.getCommandName());

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                                    oos.writeObject(response);
                                    oos.flush();
                                    byte[] responseData = baos.toByteArray();


                                    channel.send(ByteBuffer.wrap(responseData), clientAddress);
                                    if(reqIds.size() > maxSize){
                                        String old = reqIds.keySet().iterator().next();
                                        reqIds.remove(old);
                                    }
                                    reqIds.put(request.getRequestId().toString(), responseData);
                                    logger.info("Ответ отправлен клиенту {}", clientAddress);
                                }

                            }
                            catch(Exception e){
                                logger.error("Ошибка при обработке запроса ", e);
                            }
                            finally{
                                MDC.clear();
                            }


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




