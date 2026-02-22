package proglab5.commands;

import proglab5.managers.CollectionManager;
import proglab5.models.Movie;

public class Show implements Command {
    private final CollectionManager collectionmanager;

    public Show(CollectionManager collectionmanager){
        this.collectionmanager = collectionmanager;
    }

    @Override 
    public String getName(){
        return "show";
    }

    @Override
    public void execute(String... args){
        if(collectionmanager.getCollection().isEmpty()){
            System.out.println("Коллекция пуста");
        }
        else{
            for(Movie movie : collectionmanager.getCollection()){
                System.out.println(movie);
            }
        }

    }

    
}
