package proglab5.commands;

import java.util.Collections;
import java.util.Scanner;

import proglab5.managers.CollectionManager;
import proglab5.models.*;

public class AddIfMax implements Command {
    private final CollectionManager cm;
    private final Scanner scanner;

    public AddIfMax(Scanner scanner, CollectionManager cm){
        this.scanner = scanner;
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "add_if_max";
    }

    @Override
    public void execute(String... args){
        Movie movie = Ask.askMovie(scanner, cm);
        movie.setId(cm.generateId());
        if(!movie.validate()){
            System.out.println("Некоректный ввод фильма");
        }
        else if(cm.getCollection().isEmpty()){
            cm.addMovie(movie);
            System.out.println("Фильм добавлен");
        }
        else if((Collections.max(cm.getCollection())).compareTo(movie) < 0){
            cm.addMovie(movie);
            System.out.println("Фильм добавлен");
        }
        else{
            System.out.println("Фильм меньше максимального в коллекции");
        }
    }
    
}
