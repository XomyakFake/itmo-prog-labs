package ru.itmo.server.commands;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.modules.CollectionManager;

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
    public Response execute(Request request){
        collectionmanager.clearCollection();
        return new Response(true, "Коллекция очищена", null);
    }
    
}
