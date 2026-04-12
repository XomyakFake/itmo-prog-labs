package ru.itmo.server.java.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ru.itmo.common.models.Movie;
import ru.itmo.common.network.*;
import ru.itmo.server.java.modules.CollectionManager;

/**
 * Команда 'print_descnending'.Выводит элементы коллекции в порядке убывания
 * @author XomyakFake
 */
public class PrintDescending implements Command {
    private final CollectionManager cm;

    public PrintDescending(CollectionManager cm){
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "print_descending";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "вывести элементы коллекции в порядке убывания";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) { 
        if(cm.getCollection().isEmpty()){
            return new Response(true, "Коллекция пуста", null);
        }
        List<Movie> list = new ArrayList<>(cm.getCollection());
        String data = list.stream().sorted(Comparator.reverseOrder()).map(Movie::toString).collect(Collectors.joining("\n"));
        return new Response(true, "Элементы коллекции в порядке убывания", data);

    }
    
}
