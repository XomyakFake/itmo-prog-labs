package ru.itmo.client.gui;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import ru.itmo.common.network.JsonConverter;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.common.network.User;
import ru.itmo.client.util.PasswordHasher;

public class LoginFrame extends JFrame {
    private JLabel labelUsername, labelPassword, labelCaptcha;
    private JTextField textFieldUsername, textFieldCaptcha;
    private JPasswordField passwordField;
    private JButton buttonLogin, buttonRegister;
    private JComboBox<String> comboBoxLanguage;

    private int captchaResult;
    private final Random random = new Random();
    private ResourceBundle resourceBundle;
    private boolean isCaptchaVisible = false;

    private final InetAddress serverHost;
    private final int serverPort;
    private Locale currentLocale = Locale.of("ru");

    public LoginFrame(InetAddress host, int port) {
        this.serverHost = host;
        this.serverPort = port;
        setLanguage(Locale.of("ru"));
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 320);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; 
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Language:"), gbc);

        String[] languages = {"Русский", "Deutsch", "Svenska", "Español"};
        comboBoxLanguage = new JComboBox<>(languages);
        comboBoxLanguage.addActionListener(e -> {
            switch (comboBoxLanguage.getSelectedIndex()) {
                case 0 -> setLanguage(Locale.of("ru"));
                case 1 -> setLanguage(Locale.of("de"));
                case 2 -> setLanguage(Locale.of("sv"));
                case 3 -> setLanguage(Locale.of("es", "ES"));
            }
            updateTexts();
        });
        gbc.gridx = 1; 
        gbc.gridy = 0;
        mainPanel.add(comboBoxLanguage, gbc);

        labelUsername = new JLabel();
        gbc.gridx = 0; 
        gbc.gridy = 1;
        mainPanel.add(labelUsername, gbc);
        textFieldUsername = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(textFieldUsername, gbc);

        labelPassword = new JLabel();
        gbc.gridx = 0; 
        gbc.gridy = 2;
        mainPanel.add(labelPassword, gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1; 
        gbc.gridy = 2;
        mainPanel.add(passwordField, gbc);

        labelCaptcha = new JLabel();
        labelCaptcha.setVisible(false);
        gbc.gridx = 0; 
        gbc.gridy = 3;
        mainPanel.add(labelCaptcha, gbc);
        textFieldCaptcha = new JTextField(15);
        textFieldCaptcha.setVisible(false);
        gbc.gridx = 1; gbc.gridy = 3;
        mainPanel.add(textFieldCaptcha, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonLogin = new JButton();
        buttonRegister = new JButton();
        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonRegister);

        gbc.gridx = 0; 
        gbc.gridy = 4; 
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        buttonLogin.addActionListener(e -> sendAuthRequest(false));
        buttonRegister.addActionListener(e -> sendAuthRequest(true));

        add(mainPanel);
        updateTexts();
    }

    private void generateCaptcha() {
        int a = random.nextInt(20) + 1;
        int b = random.nextInt(20) + 1;
        captchaResult = a + b;
        labelCaptcha.setText(resourceBundle.getString("label.captcha") + " " + a + " + " + b + " =");
    }

    private void setLanguage(Locale locale) {
        this.currentLocale = locale;
        this.resourceBundle = ResourceBundle.getBundle("gui", locale, this.getClass().getClassLoader());

    }

    private void updateTexts() {
        setTitle(resourceBundle.getString("title.login"));
        labelUsername.setText(resourceBundle.getString("label.username"));
        labelPassword.setText(resourceBundle.getString("label.password"));
        buttonLogin.setText(resourceBundle.getString("button.login"));
        buttonRegister.setText(resourceBundle.getString("button.register"));
        if (isCaptchaVisible) generateCaptcha();
    }

    private void sendAuthRequest(boolean isRegistration) {
        String username = textFieldUsername.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, resourceBundle.getString("error.empty_fields"), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isRegistration && !isCaptchaVisible) {
            isCaptchaVisible = true;
            generateCaptcha();
            labelCaptcha.setVisible(true);
            textFieldCaptcha.setVisible(true);
            revalidate();
            repaint();
            return;
        }

        if (!isRegistration) {
            String captchaInput = textFieldCaptcha.getText().trim();
            try {
                if (captchaInput.isEmpty() || Integer.parseInt(captchaInput) != captchaResult) {
                    JOptionPane.showMessageDialog(this, resourceBundle.getString("error.wrong_captcha"), "Error", JOptionPane.ERROR_MESSAGE);
                    generateCaptcha();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, resourceBundle.getString("error.wrong_captcha"), "Error", JOptionPane.ERROR_MESSAGE);
                generateCaptcha();
                return;
            }
        }

        buttonLogin.setEnabled(false);
        buttonRegister.setEnabled(false);

        try {
            User user = new User(username, PasswordHasher.getHash(password));
            Request authRequest = new Request(user, isRegistration);

            NetworkWorker worker = new NetworkWorker(authRequest, serverHost, serverPort, new NetworkWorker.NetworkCallback() {
                @Override
                public void onSuccess(String responseJson) {
                    buttonLogin.setEnabled(true);
                    buttonRegister.setEnabled(true);
                    try {
                        Response response = JsonConverter.fromJson(responseJson, Response.class);
                        if (response.isSuccess()) {
                            String token = response.getToken();
                            String user = textFieldUsername.getText().trim();
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    System.out.println("Создаём MainFrame для: " + user);
                                    MainFrame frame = new MainFrame(user, token, serverHost, serverPort, currentLocale);
                                    System.out.println("MainFrame создан");
                                    frame.setVisible(true);
                                    System.out.println("MainFrame visible");
                                    dispose();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Ошибка запуска: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                        } else {
                            JOptionPane.showMessageDialog(LoginFrame.this, response.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Ошибка обработки ответа: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    buttonLogin.setEnabled(true);
                    buttonRegister.setEnabled(true);
                    JOptionPane.showMessageDialog(LoginFrame.this, "Ошибка соединения: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            worker.execute();

        } catch (Exception ex) {
            buttonLogin.setEnabled(true);
            buttonRegister.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Ошибка отправки: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}