package ru.itmo.client;

import java.net.InetAddress;
import javax.swing.SwingUtilities;
import ru.itmo.client.gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        try {
            InetAddress host = InetAddress.getByName("localhost");
            int port = 8000;
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginFrame(host, port).setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}