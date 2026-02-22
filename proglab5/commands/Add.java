package proglab5.commands;

import java.util.Scanner;

import proglab5.managers.CollectionManager;
import proglab5.models.*;

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

    @Override
    public void execute(String... args){
        Movie movie = Ask.askMovie(scanner, cm);
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
