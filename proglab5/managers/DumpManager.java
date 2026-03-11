package proglab5.managers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import proglab5.models.Movie;
import au.com.bytecode.opencsv.*;

/**
 * Класс для сохранения коллекций в файл и загрузки из него
 * @author XomyakFake
 */
public class DumpManager {
    private final String filename;

    /**
     * Загрузка переменной окружения в переменную имени файла
     */
    public DumpManager(){
        this.filename = System.getenv("MOVIE_FILE");
        if (filename == null || filename.isEmpty()) {
            throw new IllegalStateException("Переменная окружения MOVIE_FILE не задана");
        }
    }

    /**
     * Записывает текущее состояние коллекции в CSV файл
     * @param collection ТМенеджер коллекции данные которого нужно сохранить.
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
                System.out.println("Нечего сохранять");
            }
            else{
                System.out.println("Коллекция записана в файл");
            }
        }
        catch(FileNotFoundException e){
            System.out.println("Файл не найден или нет прав доступа");
        }
        catch(Exception e){
            System.out.println("Ошибка записи в файл");
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
                Movie movie = Movie.fromArray(list);
                boolean added = collection.addMovie(movie);
                if(added){
                    count++;}
            }
            
            if(count == 0){
                System.out.println("В файле ничего нет");}
            else{System.out.println("Коллекция прочитана из файла");}
            }
        catch(FileNotFoundException e){
            System.out.println("Файл не найден или нет прав доступа");
        }
        catch(Exception e){
            System.out.println("Ошибка чтения из файла");
        }
    }
}
