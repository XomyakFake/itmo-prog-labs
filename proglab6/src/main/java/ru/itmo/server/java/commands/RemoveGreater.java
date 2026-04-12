package ru.itmo.server.java.commands;

import ru.itmo.common.models.*;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.java.modules.CollectionManager;

/**
 * Команда 'remove_greater'. Удаляет элементы из коллекции превышающие заданный
 * @author XomyakFake
 */
public class RemoveGreater implements Command {
    private final CollectionManager cm;

    public RemoveGreater(CollectionManager cm){
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "remove_greater";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{element} удалить из коллекции все элементы, превышающие заданный";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request){
        try {
            Movie target = (Movie) request.getCommandArg();

            int bef = cm.getCollection().size();
            cm.getCollection().removeIf(m -> m.compareTo(target) > 0);

            int aft = cm.getCollection().size();

            return new Response(true, "Удалено " + (bef-aft) + " элементов.", null);

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка: Передан аргумент неправильного типа", null);
        }

    }
    
}
