package ru.itmo.client.gui;

import ru.itmo.common.models.Movie;
import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class VisualizationPanel extends JPanel {

    private static final Color[] USER_COLORS = {
        new Color(76, 175, 80),
        new Color(255, 152, 0),
        new Color(33, 150, 243),
        new Color(233, 30, 99),
        new Color(156, 39, 176),
        new Color(0, 188, 212),
    };

    private List<Movie> movies = new ArrayList<>();
    private final Map<String, Color> userColorMap = new LinkedHashMap<>();
    private Movie hoveredMovie = null;

    private OnMovieClickListener clickListener;

    private final Map<Integer, Float> animProgress = new HashMap<>();
    private Timer animTimer;

    public VisualizationPanel() {
        setBackground(Color.WHITE);
        setToolTipText("");

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Movie found = findMovieAt(e.getX(), e.getY());
                if (found != hoveredMovie) {
                    hoveredMovie = found;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Movie found = findMovieAt(e.getX(), e.getY());
                if (clickListener != null) clickListener.onMovieClick(found);
            }
        });
    }

    public void setMovies(List<Movie> movies) {
        this.movies = new ArrayList<>(movies);
        rebuildColorMap();
        startAnimation();
        repaint();
    }

    private void rebuildColorMap() {
        int colorIndex = 0;
        for (Movie m : movies) {
            String owner = m.getOwner() != null ? m.getOwner() : "unknown";
            if (!userColorMap.containsKey(owner)) {
                if (colorIndex < USER_COLORS.length) {
                    userColorMap.put(owner, USER_COLORS[colorIndex]);
                    colorIndex++;
                } else {
                    int idx = Math.abs(owner.hashCode()) % USER_COLORS.length;
                    userColorMap.put(owner, USER_COLORS[idx]);
                }
            }
        }
    }

    private void startAnimation() {
        if (animTimer != null) animTimer.stop();
        animProgress.clear();
        for (Movie m : movies) animProgress.put(m.getId(), 0f);

        animTimer = new Timer(16, e -> {
            boolean allDone = true;
            for (Movie m : movies) {
                float p = animProgress.getOrDefault(m.getId(), 1f);
                if (p < 1f) {
                    animProgress.put(m.getId(), Math.min(1f, p + 0.04f));
                    allDone = false;
                }
            }
            repaint();
            if (allDone) ((Timer) e.getSource()).stop();
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int W = getWidth(), H = getHeight();
        int PAD = 40;

        g2.setColor(new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(PAD, H - PAD, W - 10, H - PAD);
        g2.drawLine(PAD, 10, PAD, H - PAD);

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.drawString("x", W - 8, H - PAD + 4);
        g2.drawString("y", PAD - 12, 16);

        double scaleX = (double)(W - PAD - 20) / 567.0;
        double scaleY = (double)(H - PAD - 20) / 631.0;

        for (Movie m : movies) {
            double cx = PAD + m.getCoordinates().getX() * scaleX;
            double cy = H - PAD - m.getCoordinates().getY() * scaleY;

            String owner = m.getOwner() != null ? m.getOwner() : "unknown";
            Color base = userColorMap.getOrDefault(owner, Color.GRAY);

            float progress = animProgress.getOrDefault(m.getId(), 1f);
            int targetR = 6 + (int)(m.getOscarsCount() * 1.5);
            int r = (int)(targetR * progress);

            if (r <= 0) continue;

            g2.setColor(new Color(base.getRed(), base.getGreen(),
                                  base.getBlue(), 120));
            g2.fillOval((int)cx - r, (int)cy - r, r * 2, r * 2);

            g2.setStroke(new BasicStroke(m == hoveredMovie ? 2.5f : 1.5f));
            g2.setColor(m == hoveredMovie ? base.darker() : base);
            g2.drawOval((int)cx - r, (int)cy - r, r * 2, r * 2);
        }

        if (hoveredMovie != null) {
            drawTooltip(g2, hoveredMovie);
        }

        drawLegend(g2);
    }

    private void drawTooltip(Graphics2D g2, Movie m) {
        String[] lines = {
            m.getName(),
            "Владелец: " + m.getOwner(),
            "Оскары: " + m.getOscarsCount(),
            "Рейтинг: " + (m.getMpaaRating() != null ? m.getMpaaRating().name() : "—")
        };

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int tw = 0;
        for (String l : lines) tw = Math.max(tw, fm.stringWidth(l));
        int th = lines.length * (fm.getHeight() + 2) + 8;

        int PAD = 40;
        double cx = PAD + m.getCoordinates().getX() * ((getWidth() - PAD - 20) / 567.0);
        double cy = getHeight() - PAD - m.getCoordinates().getY() * ((getHeight() - PAD - 20) / 631.0);

        int tx = (int)cx + 12;
        int ty = (int)cy - th / 2;
        if (tx + tw + 12 > getWidth()) tx = (int)cx - tw - 16;

        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(tx - 4, ty - 2, tw + 12, th, 6, 6);
        g2.setColor(new Color(180, 180, 180));
        g2.drawRoundRect(tx - 4, ty - 2, tw + 12, th, 6, 6);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString(lines[0], tx, ty + fm.getAscent());
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 1; i < lines.length; i++) {
            g2.drawString(lines[i], tx, ty + fm.getAscent() + i * (fm.getHeight() + 2));
        }
    }

    private void drawLegend(Graphics2D g2) {
        if (userColorMap.isEmpty()) return;
        int x = 10, y = getHeight() - 10 - userColorMap.size() * 18;
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(x - 4, y - 4, 110, userColorMap.size() * 18 + 8, 6, 6);
        g2.setColor(new Color(180, 180, 180));
        g2.drawRoundRect(x - 4, y - 4, 110, userColorMap.size() * 18 + 8, 6, 6);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (Map.Entry<String, Color> e : userColorMap.entrySet()) {
            g2.setColor(e.getValue());
            g2.fillOval(x, y, 10, 10);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(e.getKey(), x + 14, y + 10);
            y += 18;
        }
    }

    private Movie findMovieAt(int mx, int my) {
        int W = getWidth(), H = getHeight(), PAD = 40;
        double scaleX = (double)(W - PAD - 20) / 567.0;
        double scaleY = (double)(H - PAD - 20) / 631.0;
        for (Movie m : movies) {
            double cx = PAD + m.getCoordinates().getX() * scaleX;
            double cy = H - PAD - m.getCoordinates().getY() * scaleY;
            int r = 6 + (int)(m.getOscarsCount() * 1.5);
            double dist = Math.hypot(mx - cx, my - cy);
            if (dist <= r) return m;
        }
        return null;
    }


    public Map<String, Color> getUserColorMap() { return userColorMap; }

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public void setOnMovieClickListener(OnMovieClickListener listener) {
        this.clickListener = listener;
    }
}