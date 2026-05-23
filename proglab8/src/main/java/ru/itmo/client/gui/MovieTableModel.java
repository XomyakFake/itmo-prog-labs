package ru.itmo.client.gui;

import ru.itmo.common.models.Movie;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MovieTableModel extends AbstractTableModel {

    private static final String[] COLUMN_KEYS = {
        "col.id", "col.name", "col.x", "col.y",
        "col.oscars", "col.palm", "col.tagline",
        "col.rating", "col.director", "col.owner", "col.date"
    };

    private String[] columnNames;
    private final List<Movie> movies = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT);

    public MovieTableModel(java.util.ResourceBundle bundle) {
        updateLocale(bundle);
    }

    public void updateLocale(java.util.ResourceBundle bundle) {
        Locale locale = bundle.getLocale();
        formatter = DateTimeFormatter.ofLocalizedDateTime(java.time.format.FormatStyle.SHORT).withLocale(locale.getLanguage().isEmpty() ? Locale.of("ru") : locale);
        columnNames = new String[COLUMN_KEYS.length];
        for (int i = 0; i < COLUMN_KEYS.length; i++) {
            try {
                columnNames[i] = bundle.getString(COLUMN_KEYS[i]);
            } catch (Exception e) {
                columnNames[i] = COLUMN_KEYS[i];
            }
        }
        fireTableStructureChanged();
    }

    public void setMovies(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        fireTableDataChanged();
    }

    public Movie getMovieAt(int row) {
        return movies.get(row);
    }

    public List<Movie> getMovies() {
        return new ArrayList<>(movies);
    }

    @Override public int getRowCount() { return movies.size(); }
    @Override public int getColumnCount() { return columnNames.length; }
    @Override public String getColumnName(int col) { return columnNames[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Movie m = movies.get(row);
        return switch (col) {
            case 0  -> m.getId();
            case 1  -> m.getName();
            case 2  -> m.getCoordinates().getX();
            case 3  -> m.getCoordinates().getY();
            case 4  -> m.getOscarsCount();
            case 5  -> m.getGoldenPalmCount();
            case 6  -> m.getTagline() != null ? m.getTagline() : "";
            case 7  -> m.getMpaaRating() != null ? m.getMpaaRating().name() : "";
            case 8  -> m.getDirector() != null ? m.getDirector().getName() : "";
            case 9  -> m.getOwner() != null ? m.getOwner() : "";
            case 10 -> m.getCreationDate() != null
                       ? m.getCreationDate().format(formatter) : "";
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) {
            case 0 -> Integer.class;
            case 2 -> Double.class;
            case 3 -> Integer.class;
            case 4, 5 -> Long.class;
            default -> String.class;
        };
    }
}