package ru.itmo.client;

import java.net.InetAddress;
import ru.itmo.client.util.Client;

public class Main {
    public static void main(String[] args) {
        try {
            InetAddress host = InetAddress.getByName("localhost");
            int port = 8000;
            
            Client client = new Client(host, port);
            client.run(); 
            
        } catch (Exception e) {
            System.err.println("Критическая ошибка при старте приложения:");
            e.printStackTrace();
        }
    }
}