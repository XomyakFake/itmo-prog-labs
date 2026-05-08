package ru.itmo.server.commands;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.modules.DatabaseManager;

/**
 * Команда 'clear'. Очищает коллекцию
 * @author XomyakFake
 */
public class Clear implements Command {
    private final DatabaseManager dm;

    public Clear(DatabaseManager dm){
        this.dm = dm;
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
        dm.clear(request.getUser().getUsername());
        return new Response(true, "Коллекция очищена", null);
    }
    
}
