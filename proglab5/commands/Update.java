package proglab5.commands;

import java.util.Scanner;

import proglab5.managers.CollectionManager;
import proglab5.models.*;

/**
 * Команда 'update'. Обновляет элемент коллекции по его id
 * @author XomyakFake
 */
public class Update implements Command {
    private final CollectionManager cm;
    private final Scanner scanner;

    public Update(Scanner scanner, CollectionManager cm){
        this.scanner = scanner;
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "update";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{element} обновить значение элемента коллекции, id которого равен заданному";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public void execute(String... args){
        if(args[0].isEmpty()){
            System.out.println("Не введен id");
            return;
        }
        try{
            String[] parts = args[0].strip().split(" ", 2);
            int id = Integer.parseInt(parts[0]);
            Movie del = null;
            for(Movie movie : cm.getCollection()){
                if(movie.getId() == id){
                    del = movie;
                    break;
                }
            }
            if(del != null){
                Movie movie;
                if(parts.length > 1 && !parts[1].isEmpty()){
                    movie = Movie.fromArrayNoId(parts[1].split(",",-1));
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
                movie.setId(id);
                
                if(movie.validate()){
                    cm.getCollection().remove(del);
                    cm.addMovie(movie);
                    System.out.println("Данные обновлены");
                }
                else{
                    System.out.println("Неккоректные данные. Обновление отменено");
                }

            }
            else{
                System.out.println("Такого id не существует");
            }
        }
        catch(NumberFormatException e){
            System.out.println("Некоректный id");
        }
    }
    
}
