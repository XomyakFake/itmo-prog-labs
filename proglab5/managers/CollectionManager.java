package proglab5.managers;

import proglab5.models.Movie;

import java.util.HashSet;
import java.time.ZonedDateTime;

/**
 * Оперирует коллекцией.
 * @author XomyakFake
 */
public class CollectionManager {
    private final HashSet<Movie> collection = new HashSet<>();
    private final ZonedDateTime initializationTime = ZonedDateTime.now();
    private final DumpManager dumpManager;

    public CollectionManager(DumpManager dumpManager){
        this.dumpManager = dumpManager;
    }


    /**
     * Генерирует уникальный Id для нового фильма
     * @return Уникальный Id
     */
    public Integer generateId(){
        int mxid = 0;
        for(Movie movie : collection){
            if(movie.getId() != null && movie.getId() > mxid){
                mxid = movie.getId();
            }
        }
        return mxid + 1;
    }

    /**
     * Добавляет новый фильм в коллекцию проверяя его на валидность и уникальность
     * @param movie Фильм для добавления
     * @return true если фильм добавлен, иначе false
     */
    public boolean addMovie(Movie movie){
        if(movie == null) return false;
        if(!movie.validate()){
            System.out.println("Некоректные данные");
            return false;}
        if(!isUniqueMovieId(movie.getId())){
            System.out.println("Такой ID фильма уже есть");
            return false;}
        if(movie.getDirector() != null && !isUniquePassportId(movie.getDirector().getPassportID())){
            System.out.println("Такой ID режисера уже существует");
            return false;
        }
        collection.add(movie);
        return true;
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
    }

    /**
     * Проверяет на уникальность id фильма.
     * @param id ID фильма который надо проверить.
     * @return true если id уникален, иначе false
     */
    public boolean isUniqueMovieId(Integer id){
        if(id == null) return false;
        for(Movie movie : collection){
            if(id.equals(movie.getId())){
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет на уникальность id режиссера.
     * @param id ID режиссера который надо проверить.
     * @return true если id уникален, иначе false
     */
    public boolean isUniquePassportId(String passportID){
        if(passportID == null) return true;
        for(Movie movie : collection){
            if(movie.getDirector() != null && passportID.equals(movie.getDirector().getPassportID())){
                return false;
            }
        }
        return true;
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
