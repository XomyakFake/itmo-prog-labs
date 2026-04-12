package ru.itmo.server.java.commands;

import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

import ru.itmo.common.models.Movie;
import ru.itmo.common.network.*;
import ru.itmo.server.java.modules.CollectionManager;

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
    public Response execute(Request request){
        HashSet<Movie> collection = collectionmanager.getCollection();
        if(collectionmanager.getCollection().isEmpty()){
            return new Response(true, "Коллекция пуста", null);
        }
        else{
            String data = collection.stream().sorted(Comparator.comparing(Movie::getName)).map(Movie::toString).collect(Collectors.joining("\n"));
            return new Response(true, "Элементы коллекции ", data);
        }

    }
}
