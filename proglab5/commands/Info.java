package proglab5.commands;

import proglab5.managers.CollectionManager;

public class Info implements Command {
    private final CollectionManager collectionmanager;

    public Info(CollectionManager collectionmanager){
        this.collectionmanager = collectionmanager;
    }

    @Override 
    public String getName(){
        return "info";
    }

    @Override
    public void execute(String... args){
        System.out.println("Тип: " + collectionmanager.getCollection().getClass().getSimpleName());
        System.out.println("Дата инициализации: " + collectionmanager.getInitTime());
        System.out.println("Количество элементов: " + collectionmanager.getCollection().size());

    }

}
