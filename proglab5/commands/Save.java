package proglab5.commands;

import proglab5.managers.CollectionManager;

/**
 * Команда 'save'. Сохраняет элементы коллекции в CSV файл
 * @author XomyakFake
 */
public class Save implements Command {
    private final CollectionManager collectionmanager;

    public Save(CollectionManager collectionmanager){
        this.collectionmanager = collectionmanager;
    }

    @Override 
    public String getName(){
        return "save";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "сохранить коллекцию в файл";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public void execute(String... args){
        collectionmanager.saveCollection();
    }
}
