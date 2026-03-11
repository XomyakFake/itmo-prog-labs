package proglab5.commands;

import proglab5.managers.CollectionManager;
import proglab5.models.Movie;

/**
 * Команда 'show'. Выводит все элементы коллекции
 * @author XomyakFake
 */
public class Show implements Command {
    private final CollectionManager collectionmanager;

    public Show(CollectionManager collectionmanager){
        this.collectionmanager = collectionmanager;
    }

    @Override 
    public String getName(){
        return "show";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
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
