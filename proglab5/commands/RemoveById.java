package proglab5.commands;

import proglab5.managers.CollectionManager;
import proglab5.models.Movie;

public class RemoveById implements Command {  
    private final CollectionManager cm;

    public RemoveById(CollectionManager cm){
        this.cm = cm;
    }

    @Override
    public String getName(){
        return "remove_by_id"; 
    }
    
    @Override
    public void execute(String... args) {  
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
                cm.getCollection().remove(del);
                System.out.println("Фильм удален");
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