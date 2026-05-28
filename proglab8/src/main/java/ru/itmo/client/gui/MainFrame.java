package ru.itmo.client.gui;

import ru.itmo.common.models.Movie;
import ru.itmo.common.models.MpaaRating;
import ru.itmo.common.network.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import com.fasterxml.jackson.core.type.TypeReference;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    private final String currentUser;
    private final String jwtToken;
    private final InetAddress host;
    private final int port;

    private ResourceBundle bundle;

    private MovieTableModel tableModel;
    private JTable table;
    private TableRowSorter<MovieTableModel> sorter;
    private JTextField filterField;
    private JComboBox<String> ratingFilter;
    private final Set<String> runningScripts = new HashSet<>();

    private VisualizationPanel visPanel;

    private JList<String> commandList;
    private JButton btnEdit, btnDelete, btnLogout;

    private JLabel labelUser;
    private JComboBox<String> langCombo;

    private Timer pollingTimer;

    public MainFrame(String username, String token, InetAddress host, int port) {
        this.currentUser = username;
        this.jwtToken = token;
        this.host = host;
        this.port = port;
        applyLocale(Locale.of("ru"));
        initUI();
        startPolling();
    }

    private void applyLocale(Locale locale) {
        this.bundle = ResourceBundle.getBundle("gui", locale, this.getClass().getClassLoader());
    }

    private void initUI() {
        setTitle(bundle.getString("title.main"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        bar.setBackground(new Color(240, 240, 240));
        bar.setPreferredSize(new Dimension(0, 36));

        labelUser = new JLabel("  👤 " + currentUser);
        labelUser.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bar.add(labelUser, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        right.setOpaque(false);

        JLabel langLabel = new JLabel(bundle.getString("label.language") + ":");
        langLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        right.add(langLabel);

        String[] langs = {"Русский", "Deutsch", "Svenska", "Español"};
        langCombo = new JComboBox<>(langs);
        langCombo.addActionListener(e -> {
            Locale loc = switch (langCombo.getSelectedIndex()) {
                case 1 -> Locale.of("de");
                case 2 -> Locale.of("sv");
                case 3 -> Locale.of("es", "ES");
                default -> Locale.of("ru");
            };
            applyLocale(loc);
            updateAllTexts();
        });
        right.add(langCombo);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        sidebar.setBackground(new Color(235, 235, 235));

        String[] commands = {
            "help", "info", "show", "add", "clear",
            "execute_script", "add_if_max",
            "filter_greater_than_mpaa_rating", "history",
            "print_descending", "print_field_descending_tagline",
            "remove_greater"
        };
        commandList = new JList<>(commands);
        commandList.setFont(new Font("Monospaced", Font.PLAIN, 11));
        commandList.setBackground(new Color(235, 235, 235));
        commandList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String cmd = commandList.getSelectedValue();
                    if (cmd != null) handleCommand(cmd);
                }
            }
        });

        JScrollPane listScroll = new JScrollPane(commandList);
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        sidebar.add(listScroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(new Color(235, 235, 235));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        btnEdit = new JButton(bundle.getString("button.edit"));
        btnDelete = new JButton(bundle.getString("button.delete"));
        btnLogout = new JButton(bundle.getString("button.logout"));
        btnLogout.setForeground(new Color(180, 0, 0));

        for (JButton b : new JButton[]{btnEdit, btnDelete, btnLogout}) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnPanel.add(b);
            btnPanel.add(Box.createVerticalStrut(4));
        }

        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnLogout.addActionListener(e -> logout());

        sidebar.add(btnPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    private JTabbedPane buildMainContent() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab(bundle.getString("tab.table"), buildTablePanel());
        tabs.addTab(bundle.getString("tab.visualization"), buildVisPanel());

        return tabs;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JLabel filterLabel = new JLabel(bundle.getString("label.filter") + ":");
        filterField = new JTextField(20);
        filterField.setToolTipText(bundle.getString("tooltip.filter"));

        String[] ratings = {"—", "G", "PG", "PG_13", "R", "NC_17"};
        ratingFilter = new JComboBox<>(ratings);

        filterLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        filterRow.add(filterLabel);
        filterRow.add(filterField);
        filterRow.add(new JLabel("MPAA:"));
        filterRow.add(ratingFilter);

        tableModel = new MovieTableModel(bundle);
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        ratingFilter.addActionListener(e -> applyFilter());

        panel.add(filterRow, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void applyFilter() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        String text = filterField.getText().trim();
        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + text, 1));
        }
        String rating = (String) ratingFilter.getSelectedItem();
        if (rating != null && !rating.equals("—")) {
            String safeRating = rating.replace("_", "[-_ ]?"); 
            filters.add(RowFilter.regexFilter("(?i)" + safeRating, 7));
        }
        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private void selectMovieInTable(Movie movie) {
    if (movie == null) {
        table.clearSelection();
        return;
    }
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        int viewRow = table.convertRowIndexToView(i);
        if (viewRow < 0) continue;
        Movie m = tableModel.getMovieAt(i);
        if (m.getId().equals(movie.getId())) {
            table.setRowSelectionInterval(viewRow, viewRow);
            table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
            Container parent = table.getParent();
            while (parent != null && !(parent instanceof JTabbedPane)) {
                parent = parent.getParent();
            }
            if (parent instanceof JTabbedPane tabs) {
                tabs.setSelectedIndex(0);
            }
            return;
        }
    }
    }

    private JPanel buildVisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        visPanel = new VisualizationPanel();
        visPanel.setOnMovieClickListener(movie -> {
            selectMovieInTable(movie);
        });

        panel.add(visPanel, BorderLayout.CENTER);
        return panel;
    }


       

    private void startPolling() {
        pollingTimer = new Timer(15000, e -> fetchCollection());
        pollingTimer.setInitialDelay(0);
        pollingTimer.start();
    }

    private void fetchCollection() {
        User user = new User(currentUser, "");
        Request req = new Request("show", null, user);
        req.setToken(jwtToken);

        try {
            NetworkWorker worker = new NetworkWorker(req, host, port, new NetworkWorker.NetworkCallback() {
                @Override
                public void onSuccess(String json) {
                    try {
                        Response resp = JsonConverter.fromJson(json, Response.class);
                        if (resp.isSuccess() && resp.getCollection() != null) {
                            List<Movie> movies = parseMovies(resp.getCollection());
                            SwingUtilities.invokeLater(() -> {
                                tableModel.setMovies(movies);
                                visPanel.setMovies(movies);
                            });
                        }
                        else if (resp.isSuccess() && resp.getCollection() == null) {
                            SwingUtilities.invokeLater(() -> {
                                tableModel.setMovies(new ArrayList<>());
                                visPanel.setMovies(new ArrayList<>());
                            });
                        } 
                    }
                    catch (Exception ex) {
                    }
                }
                @Override
                public void onError(Throwable e) { }
            });
            worker.execute();
        } catch (Exception ex) {
        }
    }

    private List<Movie> parseMovies(String raw) {
        if (raw == null || raw.isBlank()) return new ArrayList<>();
        try {
            return JsonConverter.getMapper().readValue(raw, new TypeReference<List<Movie>>() {}
            );
        } catch (Exception e) {
            System.err.println("[parseMovies] Ошибка: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void handleCommand(String cmd) {
        switch (cmd) {
            case "add" -> openAddDialog();
            case "clear" -> sendSimpleCommand("clear");
            case "info"  -> sendSimpleCommandWithResult("info");
            case "help"  -> sendSimpleCommandWithResult("help");
            case "history" -> sendSimpleCommandWithResult("history");
            case "print_descending" -> sendSimpleCommandWithResult("print_descending");
            case "print_field_descending_tagline" -> sendSimpleCommandWithResult("print_field_descending_tagline");
            case "show"  -> fetchCollection();
            case "add_if_max" -> openAddDialog();
            case "filter_greater_than_mpaa_rating" -> openRatingFilterDialog();
            case "remove_greater" -> openAddDialog();
            case "execute_script" -> openScriptDialog();
        }
    }

    private void sendSimpleCommand(String name) {
        User user = new User(currentUser, "");
        Request req = new Request(name, null, user);
        req.setToken(jwtToken);
        try {
            new NetworkWorker(req, host, port, new NetworkWorker.NetworkCallback() {
                @Override public void onSuccess(String json) {
                    try {
                        Response r = JsonConverter.fromJson(json, Response.class);
                        SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(MainFrame.this, r.getMessage()));
                                if (r.isSuccess() && "clear".equals(name)) {
                                tableModel.setMovies(new ArrayList<>());
                                visPanel.setMovies(new ArrayList<>());
                            }
                    } catch (Exception e) { }
                }
                @Override public void onError(Throwable e) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(MainFrame.this, "Ошибка: " + e.getMessage()));
                }
            }).execute();
        } catch (Exception ex) { }
    }

    private void sendSimpleCommandWithResult(String name) {
        User user = new User(currentUser, "");
        Request req = new Request(name, null, user);
        req.setToken(jwtToken);
        try {
            new NetworkWorker(req, host, port, new NetworkWorker.NetworkCallback() {
                @Override
                public void onSuccess(String json) {
                    try {
                        Response r = JsonConverter.fromJson(json, Response.class);
                        SwingUtilities.invokeLater(() -> {
                            String text = r.getMessage();
                            if (r.getCollection() != null && !r.getCollection().isEmpty()) {
                                text = text + "\n\n" + r.getCollection();
                            }
                            JTextArea area = new JTextArea(text);
                            area.setEditable(false);
                            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                            JScrollPane scroll = new JScrollPane(area);
                            scroll.setPreferredSize(new Dimension(500, 350));
                            JOptionPane.showMessageDialog(MainFrame.this,
                                scroll, name, JOptionPane.INFORMATION_MESSAGE);
                        });
                    } catch (Exception e) {
                        System.err.println("Ошибка: " + e.getMessage());
                    }
                }
                @Override
                public void onError(Throwable e) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(MainFrame.this,
                            "Ошибка: " + e.getMessage()));
                }
            }).execute();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void openAddDialog() {
        Movie movie = MovieDialog.showAddDialog(this, bundle, currentUser);
        if (movie == null) return;
        movie.setOwner(currentUser);

        String cmd = commandList.getSelectedValue();
        if (cmd == null) cmd = "add";

        User user = new User(currentUser, "");
        Request req = new Request(cmd, movie, user);
        req.setToken(jwtToken);

        try {
            new NetworkWorker(req, host, port, new NetworkWorker.NetworkCallback() {
                @Override
                public void onSuccess(String json) {
                    try {
                        Response r = JsonConverter.fromJson(json, Response.class);
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(MainFrame.this, r.getMessage());
                            if (r.isSuccess()) fetchCollection();
                        });
                    } catch (Exception e) { }
                }
                @Override
                public void onError(Throwable e) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(MainFrame.this,
                            "Ошибка: " + e.getMessage()));
                }
            }).execute();
        } catch (Exception ex) { }
    }

    private void openRatingFilterDialog() {
        JDialog dialog = new JDialog(this, 
            bundle.getString("dialog.filter.title"), true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        content.add(new JLabel(bundle.getString("col.rating") + ":"));

        JComboBox<MpaaRating> ratingBox = new JComboBox<>(MpaaRating.values());
        content.add(ratingBox);
        dialog.add(content, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton ok = new JButton(bundle.getString("button.ok"));
        JButton cancel = new JButton(bundle.getString("button.cancel"));
        btnPanel.add(cancel);
        btnPanel.add(ok);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dialog.dispose());
        ok.addActionListener(e -> {
            MpaaRating selected = (MpaaRating) ratingBox.getSelectedItem();
            dialog.dispose();
            if (selected == null) return;

            User user = new User(currentUser, "");
            Request req = new Request("filter_greater_than_mpaa_rating", selected.name(), user);
            req.setToken(jwtToken);

            try {
                new NetworkWorker(req, host, port, new NetworkWorker.NetworkCallback() {
                    @Override
                    public void onSuccess(String json) {
                        try {
                            Response r = JsonConverter.fromJson(json, Response.class);
                            SwingUtilities.invokeLater(() -> {
                                if (r.isSuccess() && r.getCollection() != null
                                        && !r.getCollection().isEmpty()) {
                                    JTextArea area = new JTextArea(r.getCollection());
                                    area.setEditable(false);
                                    area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                                    JScrollPane scroll = new JScrollPane(area);
                                    scroll.setPreferredSize(new Dimension(500, 300));
                                    JOptionPane.showMessageDialog(MainFrame.this,
                                        scroll, bundle.getString("dialog.filter.title"),
                                        JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(MainFrame.this,
                                        r.getMessage());
                                }
                            });
                        } catch (Exception ex) { }
                    }
                    @Override public void onError(Throwable e) {}
                }).execute();
            } catch (Exception ex) { }
        });

        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(),KeyStroke.getKeyStroke("ESCAPE"),JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.setVisible(true);
    }

    private void openScriptDialog() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(bundle.getString("dialog.script.title"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fc.getSelectedFile();
        if (!file.exists() || !file.canRead()) {
            JOptionPane.showMessageDialog(this,
                bundle.getString("error.script.read"),
                bundle.getString("error.title"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String absPath = file.getAbsolutePath();
        if (runningScripts.contains(absPath)) {
            JOptionPane.showMessageDialog(this,
                bundle.getString("error.script.recursion"),
                bundle.getString("error.title"),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        runningScripts.add(absPath);
        JDialog progressDialog = buildProgressDialog();
        progressDialog.setVisible(true);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(
                            new java.io.FileInputStream(file), "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.strip();
                        if (line.isEmpty()) continue;

                        String[] parts = line.split(" ", 2);
                        String cmd = parts[0].toLowerCase();
                        String arg = parts.length > 1 ? parts[1].strip() : null;

                        publish(cmd);
                        executeScriptCommand(cmd, arg);

                        Thread.sleep(200);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                runningScripts.remove(absPath);
                progressDialog.dispose();
                try {
                    get();
                    JOptionPane.showMessageDialog(MainFrame.this,
                        bundle.getString("script.done"));
                    fetchCollection();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                        bundle.getString("error.script.failed") + ": " + e.getMessage(),
                        bundle.getString("error.title"),
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("error.no_selection"));
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        Movie m = tableModel.getMovieAt(modelRow);
        if (!currentUser.equals(m.getOwner())) {
            JOptionPane.showMessageDialog(this, bundle.getString("error.not_owner"));
            return;
        }

        Movie updatedMovie = MovieDialog.showEditDialog(this, bundle, currentUser, m);
        if (updatedMovie == null) {
            return;
        }

        updatedMovie.setOwner(currentUser);

        User user = new User(currentUser, "");
        Request req = new Request("update", updatedMovie, user);
        req.setToken(jwtToken);

        try {
            new NetworkWorker(req, host, port, new NetworkWorker.NetworkCallback() {
                @Override
                public void onSuccess(String json) {
                    try {
                        Response r = JsonConverter.fromJson(json, Response.class);
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(MainFrame.this, r.getMessage());
                            if (r.isSuccess()) {
                                fetchCollection(); 
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(MainFrame.this, 
                            bundle.getString("error.title") + ": " + e.getMessage()));
                }
            }).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("error.no_selection"));
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        Movie m = tableModel.getMovieAt(modelRow);
        if (!currentUser.equals(m.getOwner())) {
            JOptionPane.showMessageDialog(this, bundle.getString("error.not_owner"));
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            bundle.getString("confirm.delete") + " \"" + m.getName() + "\"?",
            bundle.getString("button.delete"), JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            User user = new User(currentUser, "");
            Request req = new Request("remove_by_id", String.valueOf(m.getId()), user);
            req.setToken(jwtToken);
            try {
                new NetworkWorker(req, host, port, new NetworkWorker.NetworkCallback() {
                    @Override public void onSuccess(String json) { fetchCollection(); }
                    @Override public void onError(Throwable e) {}
                }).execute();
            } catch (Exception ex) {}
        }
    }

    private void logout() {
        pollingTimer.stop();
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame(host, port).setVisible(true));
    }

    private void updateAllTexts() {
        setTitle(bundle.getString("title.main"));
        tableModel.updateLocale(bundle);
        btnEdit.setText(bundle.getString("button.edit"));
        btnDelete.setText(bundle.getString("button.delete"));
        btnLogout.setText(bundle.getString("button.logout"));
        repaint();
    }

    private void executeScriptCommand(String cmd, String arg) {
    if (cmd.equals("exit")) {
        SwingUtilities.invokeLater(this::logout);
        return;
    }

    if (cmd.equals("execute_script")) {
        return;
    }

    try {
        Request req = buildRequestFromScript(cmd, arg);
        if (req == null) return;
        req.setToken(jwtToken);

        String json = sendSync(req);
        if (json != null) {
            Response r = JsonConverter.fromJson(json, Response.class);
            if (!r.isSuccess()) {
                System.err.println("[script] " + cmd + ": " + r.getMessage());
            }
        }
    } catch (Exception e) {
        System.err.println("[script] Ошибка команды " + cmd + ": " + e.getMessage());
    }
}

private Request buildRequestFromScript(String cmd, String arg) {
    User user = new User(currentUser, "");

    switch (cmd) {
        case "add":
        case "add_if_max":
        case "remove_greater":
            if (arg == null) return null;
            Movie movie = Movie.fromArrayNoId(arg.split(",", -1));
            if (movie == null || !movie.validate()) return null;
            
            movie.setOwner(currentUser); 
            
            return new Request(cmd, movie, user);

        case "update":
            if (arg == null) return null;
            String[] parts = arg.split(",", 2);
            if (parts.length < 2) return null;
            try {
                int id = Integer.parseInt(parts[0].strip());
                Movie updateMovie = Movie.fromArrayNoId(parts[1].split(",", -1));
                if (updateMovie == null || !updateMovie.validate()) return null;
                
                updateMovie.setId(id);
                updateMovie.setOwner(currentUser); 
                
                return new Request(cmd, updateMovie, user);
            } catch (NumberFormatException e) {
                return null;
            }

        case "remove_by_id":
        case "filter_greater_than_mpaa_rating":
            if (arg == null) return null;
            return new Request(cmd, arg, user);

        case "help":
        case "info":
        case "show":
        case "clear":
        case "history":
        case "print_descending":
        case "print_field_descending_tagline":
            return new Request(cmd, null, user);

        default:
            return null;
    }
}

    private String sendSync(Request req) {
    try {
        String json = JsonConverter.toJson(req);
        byte[] bytes = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        try (java.net.DatagramSocket socket = new java.net.DatagramSocket()) {
            socket.setSoTimeout(3000);

            DatagramPacket send = new DatagramPacket(bytes, bytes.length, host, port);
            socket.send(send);

            ByteArrayOutputStream chunks =new ByteArrayOutputStream();
            while (true) {
                byte[] buf = new byte[60001];
                java.net.DatagramPacket recv = new java.net.DatagramPacket(buf, buf.length);
                socket.receive(recv);
                boolean isLast = buf[recv.getLength() - 1] == 1;
                chunks.write(buf, 0, recv.getLength() - 1);
                if (isLast) break;
            }
            return chunks.toString(StandardCharsets.UTF_8).trim();
        }
    } catch (Exception e) {
        System.err.println("[sendSync] Ошибка: " + e.getMessage());
        return null;
    }
    }

    private JDialog buildProgressDialog() {
    JDialog d = new JDialog(this, bundle.getString("script.running"), false);
    d.setSize(280, 100);
    d.setLocationRelativeTo(this);
    d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    d.setLayout(new BorderLayout(8, 8));

    JProgressBar bar = new JProgressBar();
    bar.setIndeterminate(true);
    bar.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

    JLabel label = new JLabel(bundle.getString("script.running"), SwingConstants.CENTER);
    label.setFont(new Font("SansSerif", Font.PLAIN, 12));

    d.add(label, BorderLayout.NORTH);
    d.add(bar, BorderLayout.CENTER);
    return d;
    }

}