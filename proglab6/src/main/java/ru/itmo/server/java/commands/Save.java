package ru.itmo.server.java.commands;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.java.modules.CollectionManager;

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
    public Response execute(Request request){
        collectionmanager.saveCollection();
        return new Response(true, "Коллекция сохранена в файл", null);
    }
}
