package proglab5.commands;

import java.util.Scanner;

import proglab5.managers.CollectionManager;
import proglab5.models.*;

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

    @Override
    public void execute(String... args){
        if(args[0].isEmpty()){
        System.out.println("Не введен id");
        return;
        }
        try{
            int id = Integer.parseInt(args[0]);
            Movie del = null;
            for(Movie movie : cm.getCollection()){
                if(movie.getId().equals(id)){
                    del = movie;
                    break;
                }
            }
            if(del != null){
                Movie new_movie = Ask.askMovie(scanner, cm);
                new_movie.setId(id);
                cm.getCollection().remove(del);
                cm.addMovie(new_movie);

                System.out.println("Данные обновлены");
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
