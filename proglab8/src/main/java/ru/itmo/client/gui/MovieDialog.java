package ru.itmo.client.gui;

import ru.itmo.common.models.Color;
import ru.itmo.common.models.Country;
import ru.itmo.common.models.Coordinates;
import ru.itmo.common.models.Movie;
import ru.itmo.common.models.MpaaRating;
import ru.itmo.common.models.Person;

import javax.swing.*;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

public class MovieDialog extends JDialog {

    private boolean confirmed = false;
    private Movie result = null;
    private final ResourceBundle bundle;

    private JTextField fieldName;
    private JTextField fieldX;
    private JTextField fieldY;
    private JTextField fieldOscars;
    private JTextField fieldPalm;
    private JTextField fieldTagline;
    private JComboBox<MpaaRating> fieldRating;

    private JTextField fieldDirName;
    private JTextField fieldDirPassport;
    private JComboBox<String> fieldDirEye;
    private JComboBox<String> fieldDirHair;
    private JComboBox<String> fieldDirNationality;

    public MovieDialog(Frame parent, ResourceBundle bundle, Movie existing) {
        super(parent, true);
        this.bundle = bundle;
        initUI(existing);
    }

    private void initUI(Movie existing) {
        setTitle(existing == null
            ? bundle.getString("dialog.add.title")
            : bundle.getString("dialog.edit.title"));
        setSize(460, 580);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(0, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        fieldName = addRow(form, gbc, row++,
            bundle.getString("col.name"), "");
        fieldX = addRow(form, gbc, row++, "X", "");
        fieldY = addRow(form, gbc, row++, "Y", "");
        fieldOscars = addRow(form, gbc, row++,
            bundle.getString("col.oscars"), "");
        fieldPalm = addRow(form, gbc, row++,
            bundle.getString("col.palm"), "");
        fieldTagline = addRow(form, gbc, row++,
            bundle.getString("col.tagline"), "");

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(bundle.getString("col.rating") + ":"), gbc);
        fieldRating = new JComboBox<>(MpaaRating.values());
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(fieldRating, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JLabel dirLabel = new JLabel(bundle.getString("col.director"));
        dirLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        dirLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, java.awt.Color.LIGHT_GRAY));
        form.add(dirLabel, gbc);
        gbc.gridwidth = 1;

        fieldDirName = addRow(form, gbc, row++,
            bundle.getString("dialog.dir.name"), "");
        fieldDirPassport = addRow(form, gbc, row++,
            bundle.getString("dialog.dir.passport"), "");

        String[] colors = buildColorOptions();
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(bundle.getString("dialog.dir.eye") + ":"), gbc);
        fieldDirEye = new JComboBox<>(colors);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(fieldDirEye, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(bundle.getString("dialog.dir.hair") + ":"), gbc);
        fieldDirHair = new JComboBox<>(colors);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(fieldDirHair, gbc);
        row++;

        String[] countries = buildCountryOptions();
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(bundle.getString("dialog.dir.nationality") + ":"), gbc);
        fieldDirNationality = new JComboBox<>(countries);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(fieldDirNationality, gbc);
        row++;

        if (existing != null) prefill(existing);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnOk = new JButton(bundle.getString("button.ok"));
        JButton btnCancel = new JButton(bundle.getString("button.cancel"));
        btnPanel.add(btnCancel);
        btnPanel.add(btnOk);

        btnOk.addActionListener(e -> onOk(existing));
        btnCancel.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(btnOk);
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        add(new JScrollPane(form), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JTextField addRow(JPanel panel, GridBagConstraints gbc,
                               int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);
        JTextField field = new JTextField(value, 20);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
        return field;
    }

    private String[] buildColorOptions() {
        Color[] values = Color.values();
        String[] opts = new String[values.length + 1];
        opts[0] = "—";
        for (int i = 0; i < values.length; i++) opts[i + 1] = values[i].name();
        return opts;
    }

    private String[] buildCountryOptions() {
        Country[] values = Country.values();
        String[] opts = new String[values.length + 1];
        opts[0] = "—";
        for (int i = 0; i < values.length; i++) opts[i + 1] = values[i].name();
        return opts;
    }

    private void prefill(Movie m) {
        fieldName.setText(m.getName());
        fieldX.setText(String.valueOf(m.getCoordinates().getX()));
        fieldY.setText(String.valueOf(m.getCoordinates().getY()));
        fieldOscars.setText(String.valueOf(m.getOscarsCount()));
        fieldPalm.setText(String.valueOf(m.getGoldenPalmCount()));
        fieldTagline.setText(m.getTagline() != null ? m.getTagline() : "");
        if (m.getMpaaRating() != null) fieldRating.setSelectedItem(m.getMpaaRating());

        if (m.getDirector() != null) {
            Person d = m.getDirector();
            fieldDirName.setText(d.getName() != null ? d.getName() : "");
            fieldDirPassport.setText(d.getPassportID() != null ? d.getPassportID() : "");
            if (d.getEyeColor() != null)
                fieldDirEye.setSelectedItem(d.getEyeColor().name());
            if (d.getHairColor() != null)
                fieldDirHair.setSelectedItem(d.getHairColor().name());
            if (d.getNationality() != null)
                fieldDirNationality.setSelectedItem(d.getNationality().name());
        }
    }

    private void onOk(Movie existing) {
        try {
            Movie movie = buildMovie(existing);
            if (!movie.validate()) {
                showError(bundle.getString("error.invalid_data"));
                return;
            }
            result = movie;
            confirmed = true;
            dispose();
        } catch (NumberFormatException e) {
            showError(bundle.getString("error.number_format"));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private Movie buildMovie(Movie existing) {
        String name = fieldName.getText().trim();
        if (name.isEmpty()) throw new IllegalArgumentException(
            bundle.getString("error.name_empty"));

        double x = Double.parseDouble(fieldX.getText().trim());
        if (x > 567) throw new IllegalArgumentException(
            bundle.getString("error.x_range"));

        int y = Integer.parseInt(fieldY.getText().trim());
        if (y > 631) throw new IllegalArgumentException(
            bundle.getString("error.y_range"));

        long oscars = Long.parseLong(fieldOscars.getText().trim());
        if (oscars <= 0) throw new IllegalArgumentException(
            bundle.getString("error.oscars_positive"));

        long palm = Long.parseLong(fieldPalm.getText().trim());
        if (palm <= 0) throw new IllegalArgumentException(
            bundle.getString("error.palm_positive"));

        String tagline = fieldTagline.getText().trim();
        MpaaRating rating = (MpaaRating) fieldRating.getSelectedItem();

        Person director = null;
        String dirName = fieldDirName.getText().trim();
        if (!dirName.isEmpty()) {
            String passport = fieldDirPassport.getText().trim();
            if (passport.length() > 47) throw new IllegalArgumentException(
                bundle.getString("error.passport_length"));

            Color eyeColor = parseColor(
                (String) fieldDirEye.getSelectedItem());
            Color hairColor = parseColor(
                (String) fieldDirHair.getSelectedItem());
            Country nationality = parseCountry(
                (String) fieldDirNationality.getSelectedItem());

            director = new Person(
                dirName,
                passport.isEmpty() ? null : passport,
                eyeColor, hairColor, nationality
            );

            if (!director.validate()) throw new IllegalArgumentException(
                bundle.getString("error.director_invalid"));
        }

        Coordinates coords = new Coordinates(x, y);
        Movie movie = new Movie(name, coords, oscars, palm,
            tagline.isEmpty() ? null : tagline, rating, director);

        if (existing != null) {
            movie.setId(existing.getId());
            movie.setOwner(existing.getOwner());
        }

        return movie;
    }

    private Color parseColor(String s) {
        if (s == null || s.equals("—")) return null;
        return Color.valueOf(s);
    }

    private Country parseCountry(String s) {
        if (s == null || s.equals("—")) return null;
        return Country.valueOf(s);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg,
            bundle.getString("error.title"), JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmed() { return confirmed; }
    public Movie getResult() { return result; }

    public static Movie showAddDialog(Frame parent, ResourceBundle bundle, String user) {
        MovieDialog d = new MovieDialog(parent, bundle, null);
        d.setVisible(true);
        return d.isConfirmed() ? d.getResult() : null;
    }

    public static Movie showEditDialog(Frame parent, ResourceBundle bundle,
                                    String user, Movie existing) {
        MovieDialog d = new MovieDialog(parent, bundle, existing);
        d.setVisible(true);
        return d.isConfirmed() ? d.getResult() : null;
    }
}