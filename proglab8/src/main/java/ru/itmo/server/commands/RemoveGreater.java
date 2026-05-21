package ru.itmo.server.commands;

import ru.itmo.common.models.*;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.modules.CollectionManager;
import ru.itmo.server.modules.DatabaseManager;

/**
 * Команда 'remove_greater'. Удаляет элементы из коллекции превышающие заданный
 * @author XomyakFake
 */
public class RemoveGreater implements Command {
    private final CollectionManager cm;
    private final DatabaseManager dm;

    public RemoveGreater(CollectionManager cm, DatabaseManager dm){
        this.cm = cm;
        this.dm = dm;
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
            dm.removeGreater(target, request.getUser().getUsername());

            int bef = cm.getCollection().size();
            cm.getCollection().removeIf(m -> m.compareTo(target) > 0 && m.getOwner().equals(request.getUser().getUsername()));

            int aft = cm.getCollection().size();

            return new Response(true, "Удалено " + (bef-aft) + " элементов.", null);

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка: Передан аргумент неправильного типа", null);
        }

    }
    
}
