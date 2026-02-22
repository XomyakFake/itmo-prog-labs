package proglab5.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import proglab5.managers.CollectionManager;
import proglab5.models.Movie;
import java.util.Comparator;

public class PrintFieldDescendingTagline implements Command {
    private final CollectionManager cm;

    public PrintFieldDescendingTagline(CollectionManager cm){
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "print_field_descending_tagline";
    }

    @Override
    public void execute(String... args) {  
        if(cm.getCollection().isEmpty()){
            System.out.println("Коллекция пуста");
            return;
        }
        List<Movie> taglines = new ArrayList<>(cm.getCollection());
        Collections.sort(taglines,new Comparator<Movie>(){
            @Override
            public int compare(Movie m1, Movie m2){
                String tag1 = m1.getTagline();
                String tag2 = m2.getTagline();
                
                if(tag1 == null && tag2 == null) return 0;
                if(tag1 == null) return 1;
                if(tag2 == null) return -1;

                return tag2.compareTo(tag1);
            }
        });
            
        taglines.sort(Collections.reverseOrder());
        for(Movie movie : taglines){
            System.out.println("id=" + movie.getId() + ": " + movie.getTagline());
        }
    }  
    
}
