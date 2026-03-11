package proglab5.commands;

import java.util.Iterator;
import java.util.Scanner;

import proglab5.managers.CollectionManager;
import proglab5.models.*;

/**
 * Команда 'remove_greater'. Удаляет элементы из коллекции превышающие заданный
 * @author XomyakFake
 */
public class RemoveGreater implements Command {
    private final CollectionManager cm;
    private final Scanner scanner;

    public RemoveGreater(Scanner scanner, CollectionManager cm){
        this.scanner = scanner;
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "remove_greater";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{element} удалить из коллекции все элементы, превышающие заданный";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public void execute(String... args){
        Movie movie;
        if(args.length > 0 && !args[0].isEmpty()){
            movie = Movie.fromArrayNoId(args[0].split(",",-1));
            if(movie == null){
                System.out.println("Ошибка парсинга");
                return;
            }
        }
        else if(ExecuteScript.isExecuting){
            System.out.println("В строке не укзаны аргументы для команды");
            return;
        }
        else{
            movie = Ask.askMovie(scanner, cm);
        }
        int curr_size = cm.getCollection().size();
        Iterator<Movie> iterator = cm.getCollection().iterator();
        while(iterator.hasNext()){
            Movie m = iterator.next();
            if(m.compareTo(movie) > 0){
                iterator.remove();
            }
        }
        int size = cm.getCollection().size();
        System.out.println("Удалено " + (curr_size - size) + " элементов");

    }
    
}
