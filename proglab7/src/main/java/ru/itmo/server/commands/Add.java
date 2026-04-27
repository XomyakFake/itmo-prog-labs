package ru.itmo.server.commands;

import ru.itmo.common.exceptions.ValidateException;
import ru.itmo.common.models.*;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.modules.CollectionManager;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию
 * @author XomyakFake
 */
public class Add implements Command {
    private final CollectionManager cm;

    public Add(CollectionManager cm){
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "add";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{element} добавить новый элемент в коллекцию";
    }
    
    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request){
        try{
            Movie movie = (Movie) request.getCommandArg();
            movie.setId(cm.generateId());
            cm.addMovie(movie);
            return new Response(true, "Фильм добавлен", null);
        }
        catch(ValidateException e){
            return new Response(false, "Неккоректный ввод фильм", null);
        }
    }
    
}
