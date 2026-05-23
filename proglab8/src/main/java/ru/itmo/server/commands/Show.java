package ru.itmo.server.commands;

import java.util.ArrayList;
import ru.itmo.common.network.*;
import ru.itmo.server.modules.CollectionManager;

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
    public Response execute(Request request) {
        if (collectionmanager.getCollection().isEmpty()) {
            return new Response(true, "Коллекция пуста", null);
        }
        try {
        // Возвращаем JSON-массив вместо toString()
            String json = JsonConverter.toJson(
                new ArrayList<>(collectionmanager.getCollection())
            );
            return new Response(true, "Элементы коллекции", json);
         } catch (Exception e) {
            return new Response(false, "Ошибка сериализации", null);
        }
    }
}
