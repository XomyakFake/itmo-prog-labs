package ru.itmo.server.java.commands;

import java.util.stream.Collectors;

import ru.itmo.common.models.Movie;
import ru.itmo.common.network.*;
import ru.itmo.server.java.modules.CollectionManager;

import java.util.Comparator;

/**
 * Команда 'print_field_descending_tagline'. Выводит значения поля tagline всех элементов в порядке убывания
 * @author XomyakFake
 */
public class PrintFieldDescendingTagline implements Command {
    private final CollectionManager cm;

    public PrintFieldDescendingTagline(CollectionManager cm){
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "print_field_descending_tagline";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "вывести значения поля tagline всех элементов в порядке убывания";
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

        String data = cm.getCollection().stream().map(Movie::getTagline).sorted(Comparator.nullsLast(Comparator.reverseOrder())).map(tag -> tag == null ? "null" : tag).collect(Collectors.joining("\n"));
        return new Response(true, "Taglines в порядке убывания", data);
        
    }  
    
}
