package proglab5.commands;

import proglab5.managers.CollectionManager;
import proglab5.models.Movie;

/**
 * Команда 'remove_by_id'. Удаляет элемент по его id
 * @author XomyakFake
 */
public class RemoveById implements Command {  
    private final CollectionManager cm;

    public RemoveById(CollectionManager cm){
        this.cm = cm;
    }

    @Override
    public String getName(){
        return "remove_by_id"; 
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{id} удалить элемент из коллекции по его id";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
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
