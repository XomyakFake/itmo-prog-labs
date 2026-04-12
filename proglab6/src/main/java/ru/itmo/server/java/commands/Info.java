package ru.itmo.server.java.commands;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.java.modules.CollectionManager;

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
    public Response execute(Request request){
        String data = "Тип: " + collectionmanager.getCollection().getClass().getSimpleName() + "\n" + 
        "Дата инициализации: " + collectionmanager.getInitTime() + "\n" +
        "Количество элементов: " + collectionmanager.getCollection().size();
        return new Response(true, "Информация по коллекции", data);

    }

}
