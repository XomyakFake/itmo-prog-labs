package proglab5.commands;

import proglab5.managers.CollectionManager;

/**
 * Команда 'clear'. Очищает коллекцию
 * @author XomyakFake
 */
public class Clear implements Command {
    private final CollectionManager collectionmanager;

    public Clear(CollectionManager collectionmanager){
        this.collectionmanager = collectionmanager;
    }

    @Override 
    public String getName(){
        return "clear";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "очистить коллекцию";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public void execute(String... args){
        collectionmanager.clearCollection();
        System.out.println("Коллекция очищена");
    }
    
}
