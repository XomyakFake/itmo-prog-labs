package ru.itmo.server.modules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
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
import ru.itmo.common.network.User;

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
        if (db_password == null) {
            logger.error("Переменная окружения DB_PASSWORD не задана");
            throw new IllegalStateException("Переменная окружения DB_PASSWORD не задана");
        }
    }

    public void establishConnection(){
        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(db_url, db_user, db_password);
        }
        catch (SQLException e){
            logger.error("Ошибка подключения к базе данных: {}", e.getMessage());
            throw new IllegalStateException("Ошибка подключения к базе данных");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkUserExistanse(String username){
        String query = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";
        try(PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, username);
            ResultSet res = p.executeQuery();
            if (res.next()){
                return res.getBoolean(1);
            }
        }
        catch(SQLException e){
            logger.error("Ошибка выполнения запроса БД");
            return false;
        }
        return true;
    }

    public boolean checkUserPassword(User user){
        var username = user.getUsername();
        var hashedPassword = user.getPassword();

        String query = "SELECT hashed_password FROM users WHERE username = ?";
        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, username);
            ResultSet res = p.executeQuery();

            if (res.next()){
                String storedHashedPassword = res.getString("hashed_password");
                return storedHashedPassword.equals(hashedPassword);
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void addUser(User user){
        var username = user.getUsername();
        var hashedPassword = user.getPassword();

        String query = "INSERT INTO users (username, hashed_password) VALUES (?, ?)";

        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, username);
            p.setString(2, hashedPassword);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void readCollection(CollectionManager cm) {
        String query = "SELECT movies.*, users.username AS owner_username FROM movies LEFT JOIN users ON movies.owner_id = users.id";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("name");
                double x = rs.getDouble("x");
                int y = rs.getInt("y");

                ZonedDateTime creationDate = rs.getTimestamp("creation_date").toInstant().atZone(ZoneId.systemDefault());

                long oscarsCount = rs.getLong("oscars_count");
                long goldenPalmCount = rs.getLong("golden_palm_count");

                String tagline = rs.getString("tagline");
                MpaaRating mpaaRating = MpaaRating.valueOf(rs.getString("mpaa_rating"));

                Person director = null;

                if (rs.getString("director_name") != null) {
                    String directorName = rs.getString("director_name");
                    String passportId = rs.getString("director_passport_id");

                    Color eyeColor = rs.getString("director_eye_color") != null ? 
                        Color.valueOf(rs.getString("director_eye_color")) : null;

                    Color hairColor = rs.getString("director_hair_color") != null ? 
                        Color.valueOf(rs.getString("director_hair_color")) : null;

                    Country nationality = rs.getString("director_nationality") != null ? 
                        Country.valueOf(rs.getString("director_nationality")) : null;

                    director = new Person(directorName, passportId, eyeColor, hairColor, nationality);
                }

                Coordinates coordinates = new Coordinates(x, y);
                Movie movie = new Movie(name, coordinates, creationDate,
                        oscarsCount, goldenPalmCount, tagline, mpaaRating, director);

                movie.setId(id);
                movie.setOwner(rs.getString("owner_id"));

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

    public int addMovie(Movie movie, String owner){
        String query = "INSERT INTO movies (name, x, y, creation_date, oscars_count, golden_palm_count, tagline, mpaa_rating, director_name, director_passport_id, director_eye_color, director_hair_color, director_nationality, owner_id)" +
        " VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS mpaa_rating), ?, ?, CAST(? AS color), CAST(? AS color), CAST(? AS country), (SELECT id FROM users WHERE username = ?))";

        try(PreparedStatement p = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            p.setString(1, movie.getName());
            p.setDouble(2, movie.getCoordinates().getX());
            p.setInt(3, movie.getCoordinates().getY());
            p.setTimestamp(4, Timestamp.from(movie.getCreationDate().toInstant()));
            p.setLong(5, movie.getOscarsCount());
            p.setLong(6, movie.getGoldenPalmCount());
            p.setString(7, movie.getTagline());
            p.setString(8, movie.getMpaaRating().name());

            if (movie.getDirector() != null) {
                p.setString(9, movie.getDirector().getName());
                p.setString(10, movie.getDirector().getPassportID());
                p.setString(11, movie.getDirector().getEyeColor() != null ? movie.getDirector().getEyeColor().name() : null);
                p.setString(12, movie.getDirector().getHairColor() != null ? movie.getDirector().getHairColor().name() : null);
                p.setString(13, movie.getDirector().getNationality() != null ? movie.getDirector().getNationality().name() : null);
            } 
            else {
                p.setNull(9, Types.VARCHAR);
                p.setNull(10, Types.VARCHAR);
                p.setNull(11, Types.VARCHAR);
                p.setNull(12, Types.VARCHAR);
                p.setNull(13, Types.VARCHAR);
            }

            p.setString(14, owner);

            p.executeUpdate();

            ResultSet keys = p.getGeneratedKeys();
            if(keys.next()){
                return keys.getInt(1);
            }
            return -1;
        }
        catch(SQLException e){
            logger.error("Ошибка добавления объекта в БД: {}", e.getMessage());
            return -1;
        }
    }

    public boolean removeById(int id, String owner){
        String query = "DELETE FROM movies WHERE id = ? AND owner_id = (SELECT id FROM users WHERE username = ?)";
        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setLong(1, id);
            p.setString(2, owner);
            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            logger.error("Ошибка удаления объекта в БД по id: {}", e.getMessage());
            return false;
        }
    }

    public boolean clear(String owner){
        String query = "DELETE FROM movies WHERE owner_id = (SELECT id FROM users WHERE username = ?)";
        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, owner);
            p.executeUpdate();
            return true;

        } catch (SQLException e) {
            logger.error("Ошибка очистки объектов пользователя: {}", e.getMessage());
            return false;
        }
    }

    public boolean update(Movie movie, String owner){
        String query = "UPDATE movies SET name = ?, x = ?, y = ?, creation_date = ?, oscars_count = ?, golden_palm_count = ?, tagline = ?, mpaa_rating = CAST(? AS mpaa_rating), director_name = ?, director_passport_id = ?, director_eye_color = CAST(? AS color), director_hair_color = CAST(? AS color), director_nationality = CAST(? AS country) WHERE id = ? AND owner_id = (SELECT id FROM users WHERE username = ?)";
        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setString(1, movie.getName());
            p.setDouble(2, movie.getCoordinates().getX());
            p.setInt(3, movie.getCoordinates().getY());
            p.setTimestamp(4, Timestamp.from(movie.getCreationDate().toInstant()));
            p.setLong(5, movie.getOscarsCount());
            p.setLong(6, movie.getGoldenPalmCount());
            p.setString(7, movie.getTagline());
            p.setString(8, movie.getMpaaRating().name());
            p.setString(9, movie.getDirector() != null ? movie.getDirector().getName() : null);
            p.setString(10, movie.getDirector() != null ? movie.getDirector().getPassportID() : null);
            p.setString(11, movie.getDirector() != null ? movie.getDirector().getEyeColor() != null ? movie.getDirector().getEyeColor().name() : null : null);
            p.setString(12, movie.getDirector() != null ? movie.getDirector().getHairColor() != null ? movie.getDirector().getHairColor().name() : null : null);
            p.setString(13, movie.getDirector() != null ? movie.getDirector().getNationality() != null ? movie.getDirector().getNationality().name() : null : null);
            p.setInt(14, movie.getId());
            p.setString(15, owner);

            p.executeUpdate();
            return true;
        }
        catch(SQLException e){
            logger.error("Ошибка обновления объекта в БД: {}", e.getMessage());
            return false;
        }
    }

    public boolean removeGreater(Movie movie, String owner){
        String query = "DELETE FROM movies WHERE oscars_count > ? AND owner_id = (SELECT id FROM users WHERE username = ?)";
        try (PreparedStatement p = connection.prepareStatement(query)){
            p.setLong(1, movie.getOscarsCount());
            p.setString(2, owner);
            p.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка удаления объектов в БД меньше значения oscars_count: {}", e.getMessage());
            return false;
        }
    }
}