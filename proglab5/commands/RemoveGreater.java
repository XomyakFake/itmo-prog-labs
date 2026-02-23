package proglab5.commands;

import java.util.Iterator;
import java.util.Scanner;

import proglab5.managers.CollectionManager;
import proglab5.models.*;

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

    @Override
    public void execute(String... args){
        Movie movie = Ask.askMovie(scanner, cm);
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
