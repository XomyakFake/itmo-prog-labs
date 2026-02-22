package proglab5.commands;

import proglab5.managers.CollectionManager;
import proglab5.models.Movie;
import proglab5.models.MpaaRating;

public class FilterGreaterThanMpaaRating implements Command {
    private final CollectionManager cm;

    public FilterGreaterThanMpaaRating(CollectionManager cm){
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "filter_greater_than_mpaa_rating";
    }

    @Override
    public void execute(String... args) {  
        if(args[0].isEmpty()){
            System.out.println("Не введен рейтинг");
            return;
        }

        MpaaRating target ;
        try{
            target = MpaaRating.valueOf(args[0].strip());
        }
        catch(IllegalArgumentException e){
            System.out.println("Некоректный рейтинг. Доступны только: " + MpaaRating.names());
            return;
        }
        boolean found = false;
        for(Movie movie : cm.getCollection()){
            if(movie.getMpaaRating().compareTo(target) > 0){
                System.out.println(movie);
                found = true;
            }
        }
        if(!found){
            System.out.println("ФИльмы не найдены");
        }
       
    }
    
}
