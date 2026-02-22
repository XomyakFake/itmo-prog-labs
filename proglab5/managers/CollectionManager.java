package proglab5.managers;

import proglab5.models.Movie;
import java.util.HashSet;
import java.time.ZonedDateTime;

public class CollectionManager {
    private final HashSet<Movie> collection = new HashSet<>();
    private final ZonedDateTime initializationTime = ZonedDateTime.now();
    private int Id = 1;

    public Integer generateId(){
        return Id++;
    }

    public void addMovie(Movie movie){
        collection.add(movie);
    }

    public ZonedDateTime getInitTime(){
        return initializationTime;
    }

    public HashSet<Movie> getCollection(){
        return collection;
    }

    public void clearCollection(){
        collection.clear();
    }

    public boolean isUiquePassportId(String passportID){
        if(passportID == null) return true;
        for(Movie movie : collection){
            if(movie.getDirector() != null && passportID.equals(movie.getDirector().getPassportID())){
                return false;
            }
        }
        return true;
    }

}
