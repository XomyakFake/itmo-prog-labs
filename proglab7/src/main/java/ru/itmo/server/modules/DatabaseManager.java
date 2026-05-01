package ru.itmo.server.modules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.*;

import ru.itmo.common.exceptions.ValidateException;
import ru.itmo.common.models.Color;
import ru.itmo.common.models.Coordinates;
import ru.itmo.common.models.Country;
import ru.itmo.common.models.Movie;
import ru.itmo.common.models.MpaaRating;
import ru.itmo.common.models.Person;

public class DatabaseManager {
    private static Connection connection;
    private final String db_url;
    private final String db_user;
    private final String db_password;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    public DatabaseManager(){
        this.db_url = System.getenv("DB_URL");
        this.db_user = System.getenv("DB_USER");
        this.db_password = System.getenv("DB_PASSWORD");

        if (db_url == null || db_url.isEmpty()) {
            logger.error("Переменная окружения DB_URL не задана");
            throw new IllegalStateException("Переменная окружения DB_URL не задана");
        }
        if (db_user == null || db_user.isEmpty()) {
            logger.error("Переменная окружения DB_USER не задана");
            throw new IllegalStateException("Переменная окружения DB_USER не задана");
        }
        if (db_password == null || db_password.isEmpty()) {
            logger.error("Переменная окружения DB_PASSWORD не задана");
            throw new IllegalStateException("Переменная окружения DB_PASSWORD не задана");
        }

    }
    public void establishConnection(){
        try{
            connection = DriverManager.getConnection(db_url, db_user, db_password);
        }
        catch (SQLException e){
            logger.error("Ошибка подключения к базе данных");
            throw new IllegalStateException("Ошибка подключения к базе данных");
        }
        
    }

    public void readCollection(CollectionManager cm) {
        String query = "SELECT * FROM movies";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double x = rs.getDouble("x");
                int y = rs.getInt("y");
                ZonedDateTime creationDate = rs.getTimestamp("creationDate").toInstant().atZone(ZoneId.systemDefault());
                long oscarsCount = rs.getLong("oscarsCount");
                long goldenPalmCount = rs.getLong("goldenPalmCount");
                String tagline = rs.getString("tagline");
                MpaaRating mpaaRating = MpaaRating.valueOf(rs.getString("mpaarating"));

                Person director = null;
                if (rs.getString("directorName") != null) {
                    String directorName = rs.getString("directorName");
                    String passportId = rs.getString("directorPassportId");
                    Color eyeColor = rs.getString("directorEyecolor") != null ? 
                        Color.valueOf(rs.getString("directorEyecolor")) : null;
                    Color hairColor = rs.getString("directorHaircolor") != null ? 
                        Color.valueOf(rs.getString("directorHaircolor")) : null;
                    Country nationality = rs.getString("directorNationality") != null ? 
                        Country.valueOf(rs.getString("directorNationality")) : null;
                    director = new Person(directorName, passportId, eyeColor, hairColor, nationality);
                }

                Coordinates coordinates = new Coordinates(x, y);
                Movie movie = new Movie(name, coordinates, creationDate, oscarsCount, goldenPalmCount, tagline, mpaaRating, director);
                movie.setId(id);

                try {
                    cm.addMovie(movie);
                } catch (ValidateException e) {
                    logger.warn("Некорректный фильм в БД id={}", id);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка чтения коллекции из БД");
            throw new IllegalStateException(e);
        }
    }

    public void writeCollection(){

    }
}
