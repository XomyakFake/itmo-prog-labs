package ru.itmo.server.modules;

import ru.itmo.common.exceptions.ValidateException;
import ru.itmo.common.models.Movie;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

/**
 * Оперирует коллекцией.
 * @author XomyakFake
 */
public class CollectionManager {
    private final HashSet<Movie> collection = new HashSet<>();
    private final ZonedDateTime initializationTime = ZonedDateTime.now();
    private final DumpManager dumpManager;
    private Logger logger = LoggerFactory.getLogger(CollectionManager.class);

    public CollectionManager(DumpManager dumpManager){
        this.dumpManager = dumpManager;
    }


    /**
     * Генерирует уникальный Id для нового фильма
     * @return Уникальный Id
     */
    public Integer generateId(){
        return collection.stream().mapToInt(Movie::getId).max().orElse(0) + 1;
    }

    /**
     * Добавляет новый фильм в коллекцию проверяя его на валидность и уникальность
     * @param movie Фильм для добавления
     * @throws ValidateException 
     */
    public void addMovie(Movie movie) throws ValidateException{
        if(movie == null || !movie.validate()){
            logger.warn("Попытка добавить некоректный фильм");
            throw new ValidateException("Некорректные данные");
        }
        if(!isUniqueMovieId(movie.getId())) {
            logger.warn("Попытка добавить фильм c существующим ID");
            throw new ValidateException("Такой ID фильма уже есть");
        }
        if(movie.getDirector() != null && !isUniquePassportId(movie.getDirector().getPassportID())) throw new ValidateException("Такой ID режисера уже существует");
        collection.add(movie);
        logger.info("Фильм с ID={} добавлен", movie.getId());
    }

    /**
     * Возвращает текущее дату и время
     * @return Текущее дата и время
     */
    public ZonedDateTime getInitTime(){
        return initializationTime;
    }

    /**
     * Возвращает коллекцию
     * @return Коллекция
     */
    public HashSet<Movie> getCollection(){
        return collection;
    }

    /**
     * Очищает коллекцию от элементов.
     */
    public void clearCollection(){
        collection.clear();
        logger.info("Коллекция очищена");
    }

    /**
     * Проверяет на уникальность id фильма.
     * @param id ID фильма который надо проверить.
     * @return true если id уникален, иначе false
     */
    public boolean isUniqueMovieId(Integer id){
        if(id == null) return false;
        return collection.stream().noneMatch(movie -> id.equals(movie.getId()));
    }

    /**
     * Проверяет на уникальность id режиссера.
     * @param id ID режиссера который надо проверить.
     * @return true если id уникален, иначе false
     */
    public boolean isUniquePassportId(String passportID){
        if(passportID == null) return true;
        return collection.stream().filter(movie -> movie.getDirector() != null).noneMatch(movie -> passportID.equals(movie.getDirector().getPassportID()));
    }

    /**
     * Сохраняет коллекцию в CSV файл
     */
    public void saveCollection(){
        dumpManager.writeCollection(this);
    }

    /**
     * Загружает коллекцию из CSV файла
     */
    public void loadCollection(){
        dumpManager.readCollection(this);
    }

}
