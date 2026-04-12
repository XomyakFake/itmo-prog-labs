package ru.itmo.server.java.commands;

import java.util.Collections;

import ru.itmo.common.exceptions.ValidateException;
import ru.itmo.common.models.*;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.java.modules.CollectionManager;

/**
 * Команда 'add_if_max'. Добавляет новый элемент в коллекцию, 
 * если его значение превышает значение наибольшего элемента коллекции.
 * @author XomyakFake
 */

public class AddIfMax implements Command {
    private final CollectionManager cm;

    public AddIfMax(CollectionManager cm){
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "add_if_max";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{element} добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
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
            if(Collections.max(cm.getCollection()).compareTo(movie) < 0){
                cm.addMovie(movie);
                return new Response(true, "Фильм добавлен", null);}
            else{
                return new Response(true, "Фильм меньше максимального в коллекции", null);}
        }
        catch(ValidateException e){
            return new Response(false, "Неккоректный ввод фильм", null);
        }
    }
    
}
