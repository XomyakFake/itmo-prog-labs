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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Server {
    private InetSocketAddress address;
    private Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int PACKET_SIZE = 60000;
    private static final int DATA_SIZE = PACKET_SIZE - 1;
    private final ExecutorService virtualPool = Executors.newVirtualThreadPerTaskExecutor();

    public Server(InetSocketAddress address) {
        this.address = address;
    }

    private void sendChunks(DatagramChannel channel, byte[] responseData, SocketAddress clientAddress) throws IOException {
        int n = (int) Math.ceil((double) responseData.length / DATA_SIZE);
        for (int i = 0; i < n; i++) {
            int from = i * DATA_SIZE;
            int to = Math.min(from + DATA_SIZE, responseData.length);
            byte[] chunk = Arrays.copyOfRange(responseData, from, to);
            byte[] packet = new byte[chunk.length + 1];
            System.arraycopy(chunk, 0, packet, 0, chunk.length);
            packet[packet.length - 1] = (byte) (i == n - 1 ? 1 : 0);
            channel.send(ByteBuffer.wrap(packet), clientAddress);
        }
    }

    public void run() {
        DatabaseManager dm = new DatabaseManager();
        dm.establishConnection();
        CommandInvoker commandInvoker = new CommandInvoker();
        CollectionManager collectionManager = new CollectionManager();
        dm.readCollection(collectionManager);
        CommandRegistr.register(commandInvoker, collectionManager, dm);

        StorageCommands storage = new StorageCommands();
        RequestHandler requestHandler = new RequestHandler(commandInvoker, storage, dm);

        logger.info("Сервер запущен на виртуальных потоках");
        logger.info("Адрес: {}", address);

        try (DatagramChannel channel = DatagramChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            Selector selector = Selector.open();
            channel.configureBlocking(false);
            channel.bind(address);
            channel.register(selector, SelectionKey.OP_READ);

            ByteBuffer buffer = ByteBuffer.allocate(65507);

            while (true) {
                if (System.in.available() > 0) {
                    String cmd = scanner.nextLine().trim();
                    if (cmd.equals("exit")) {
                        logger.info("Сервер завершил работу");
                        System.exit(0);
                    }
                }

                selector.select(500);
                Set<SelectionKey> keys = selector.selectedKeys();
                for (var iter = keys.iterator(); iter.hasNext(); ) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (key.isValid() && key.isReadable()) {
                        buffer.clear();
                        SocketAddress clientAddress = channel.receive(buffer);
                        buffer.flip();

                        byte[] data = new byte[buffer.limit()];
                        buffer.get(data);

                        SocketAddress finalClientAddress = clientAddress;
                        virtualPool.submit(() -> {
                            try {
                                byte[] responseData = requestHandler.handle(data, finalClientAddress.toString());
                                if (responseData == null) {
                                    logger.error("Ответ пустой, не отправляем");
                                    return;
                                }
                                sendChunks(channel, responseData, finalClientAddress);
                            } catch (Exception e) {
                                logger.error("Ошибка в виртуальном потоке", e);
                            }
                        });
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Ошибка сервера", e);
        }
    }
}