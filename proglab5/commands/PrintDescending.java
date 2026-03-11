package proglab5.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import proglab5.managers.CollectionManager;
import proglab5.models.Movie;

/**
 * Команда 'print_descnending'.Выводит элементы коллекции в порядке убывания
 * @author XomyakFake
 */
public class PrintDescending implements Command {
    private final CollectionManager cm;

    public PrintDescending(CollectionManager cm){
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "print_descending";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "вывести элементы коллекции в порядке убывания";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public void execute(String... args) { 
        if(cm.getCollection().isEmpty()){
            System.out.println("Коллекция пуста");
            return;
        }
        List<Movie> list = new ArrayList<>(cm.getCollection());
        list.sort(Collections.reverseOrder());
        for(Movie movie : list){
            System.out.println(movie);
        }
        
    }
    
}
