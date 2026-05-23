package ru.itmo.client.gui;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import ru.itmo.common.network.JsonConverter;

public class NetworkWorker extends SwingWorker<String, Void> {
    private final String requestJson;
    private final InetAddress host;
    private final int port;
    private final NetworkCallback callback;

    public interface NetworkCallback {
        void onSuccess(String responseJson);
        void onError(Throwable e);
    }

    public NetworkWorker(Object requestObject, InetAddress host, int port, NetworkCallback callback) throws Exception {
        this.requestJson = JsonConverter.toJson(requestObject);
        this.host = host;
        this.port = port;
        this.callback = callback;
    }

    @Override
    protected String doInBackground() throws Exception {
        System.out.println("[NetworkWorker] Отправляем запрос на " + host + ":" + port);
        System.out.println("[NetworkWorker] JSON: " + this.requestJson);
        
        byte[] objectBytes = this.requestJson.getBytes(StandardCharsets.UTF_8);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(4000);
            
            DatagramPacket sendPacket = new DatagramPacket(objectBytes, objectBytes.length, host, port);
            socket.send(sendPacket);
            System.out.println("[NetworkWorker] Пакет отправлен, ждём ответ...");

            ByteArrayOutputStream chunks = new ByteArrayOutputStream();
            while (true) {
                byte[] buf = new byte[60001];
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(receivePacket);
                    System.out.println("[NetworkWorker] Получен чанк размером: " + receivePacket.getLength());
                } catch (SocketTimeoutException e) {
                    System.out.println("[NetworkWorker] Таймаут ожидания ответа");
                    throw new Exception("Превышено время ожидания ответа от сервера.");
                }

                boolean isLast = (buf[receivePacket.getLength() - 1] == 1);
                chunks.write(buf, 0, receivePacket.getLength() - 1);
                if (isLast) break;
            }

            String result = chunks.toString(StandardCharsets.UTF_8).trim();
            System.out.println("[NetworkWorker] Ответ получен: " + result);
            return result;
        }
    }

    @Override
    protected void done() {
        try {
            String resultJson = get();
            System.out.println("[NetworkWorker] done() вызван успешно");
            callback.onSuccess(resultJson);
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("[NetworkWorker] done() вызван с ошибкой: " + e.getMessage());
            e.printStackTrace();
            callback.onError(e.getCause() != null ? e.getCause() : e);
        }
    }
}