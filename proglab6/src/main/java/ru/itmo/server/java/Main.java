package ru.itmo.server.java;

import java.net.InetSocketAddress;

import ru.itmo.server.java.modules.Server;

public class Main {
    public static void main(String[] args){
        Server server = new Server(new InetSocketAddress(8000));
        server.run();
    }
}
