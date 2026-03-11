package proglab5.commands;

import proglab5.managers.CollectionManager;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 * @author XomyakFake
 */
public class Info implements Command {
    private final CollectionManager collectionmanager;

    public Info(CollectionManager collectionmanager){
        this.collectionmanager = collectionmanager;
    }

    @Override 
    public String getName(){
        return "info";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public void execute(String... args){
        System.out.println("Тип: " + collectionmanager.getCollection().getClass().getSimpleName());
        System.out.println("Дата инициализации: " + collectionmanager.getInitTime());
        System.out.println("Количество элементов: " + collectionmanager.getCollection().size());

    }

}
