package proglab5.commands;

import proglab5.managers.CollectionManager;

public class Clear implements Command {
    private final CollectionManager collectionmanager;

    public Clear(CollectionManager collectionmanager){
        this.collectionmanager = collectionmanager;
    }

    @Override 
    public String getName(){
        return "clear";
    }

    @Override
    public void execute(String... args){
        collectionmanager.clearCollection();
        System.out.println("Коллекция очищена");
    }
    
}
