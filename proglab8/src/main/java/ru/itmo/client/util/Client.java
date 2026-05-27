package ru.itmo.client.util;

import java.net.InetAddress;
import javax.swing.SwingUtilities;
import ru.itmo.client.gui.LoginFrame;

public class Client {
    private final InetAddress host;
    private final int port;

    public Client(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        System.out.println("[Client] Запуск клиентского приложения на порту " + port);

        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame(host, port).setVisible(true);
                System.out.println("[Client] Окно LoginFrame успешно выведено на экран.");
            } catch (Exception e) {
                System.err.println("[Client] Не удалось инициализировать GUI:");
                e.printStackTrace();
            }
        });
    }
}