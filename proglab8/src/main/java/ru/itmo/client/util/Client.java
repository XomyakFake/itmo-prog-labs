package ru.itmo.client.util;

import java.net.InetAddress;
import javax.swing.SwingUtilities;
import ru.itmo.client.gui.LoginFrame;

public class Client {
    private InetAddress host;
    private int port;

    public Client(InetAddress host, int port){
        this.host = host;
        this.port = port;
    }

    public void run(){
        System.out.println("Запуск клиентского приложения...");

        SwingUtilities.invokeLater(() -> { new LoginFrame(host, port).setVisible(true);});
    }
}