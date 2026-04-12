package ru.itmo.server.java.modules;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import ru.itmo.common.exceptions.ValidateException;
import ru.itmo.common.models.Movie;
import au.com.bytecode.opencsv.*;
import org.slf4j.*;

/**
 * Класс для сохранения коллекций в файл и загрузки из него
 * @author XomyakFake
 */
public class DumpManager {
    private final String filename;
    private static final Logger logger = LoggerFactory.getLogger(DumpManager.class);

    /**
     * Загрузка переменной окружения в переменную имени файла
     */
    public DumpManager(){
        this.filename = System.getenv("MOVIE_FILE");
        if (filename == null || filename.isEmpty()) {
            logger.error("Переменная окружения MOVIE_FILE не задана");
            throw new IllegalStateException("Переменная окружения MOVIE_FILE не задана");
        }
    }

    /**
     * Записывает текущее состояние коллекции в CSV файл
     * @param collection Менеджер коллекции данные которого нужно сохранить.
     */
    public void writeCollection(CollectionManager collection){
        try(
            FileOutputStream fos = new FileOutputStream(filename);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            CSVWriter writer = new CSVWriter(osw, ',');
        ){


            for(Movie movie : collection.getCollection()){
                writer.writeNext(Movie.toArray(movie));
            }
            if(collection.getCollection().isEmpty()){
                logger.info("Нечего сохранять");
            }
            else{
                logger.info("Коллекция записана в файл");
            }
        }
        catch(FileNotFoundException e){
            logger.error("Файл не найден или нет прав доступа");
        }
        catch(Exception e){
            logger.error("Ошибка записи в файл");
        }
    }
    /**
     * Читает коллекцию из CSV файла и заполняет ими коллекцию
     * @param collection Менеджер коллекции в который загружаются данные
     */

    public void readCollection(CollectionManager collection){
        try(
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            CSVReader reader = new CSVReader(isr, ',');
        ){
            int count = 0;
            String[] list;
            while((list = reader.readNext()) != null){
                try{
                    Movie movie = Movie.fromArray(list);
                    collection.addMovie(movie);
                    count++;
                }
                catch(ValidateException e){
                    logger.warn("Неккоректные данные фильма");
                }
                catch(Exception e){
                    logger.warn("Ошибка парсинга");
                }
                
            }
            
            if(count == 0){
                logger.warn("В файле ничего нет");}
            else{logger.info("Коллекция прочитана из файла");}
        }
        catch(FileNotFoundException e){
            logger.error("Файл не найден или нет прав доступа");
        }
        catch(Exception e){
            logger.error("Ошибка чтения из файла");
        }
    }
}
