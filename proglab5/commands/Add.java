package proglab5.commands;

import java.util.Scanner;
import proglab5.managers.CollectionManager;
import proglab5.models.*;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию
 * @author XomyakFake
 */
public class Add implements Command {
    private final CollectionManager cm;
    private final Scanner scanner;

    public Add(Scanner scanner, CollectionManager cm){
        this.scanner = scanner;
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "add";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{element} добавить новый элемент в коллекцию";
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
        movie.setId(cm.generateId());
        if(movie.validate()){
            cm.addMovie(movie);
            System.out.println("Фильм добавлен");
        }
        else{
            System.out.println("Некоректный ввод фильма");
        }
    }
    
}
