package ru.itmo.server.commands;

import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

import ru.itmo.common.models.Movie;
import ru.itmo.common.models.MpaaRating;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.modules.CollectionManager;

/**
 * Команда 'filter_greater_than_mpaa_rating'. Выводит элементы 
 * значения поля поля mpaaRating которых больше заданного
 * @author XomyakFake
 */
public class FilterGreaterThanMpaaRating implements Command {
    private final CollectionManager cm;

    public FilterGreaterThanMpaaRating(CollectionManager cm){
        this.cm = cm;
    }

    @Override 
    public String getName(){
        return "filter_greater_than_mpaa_rating";
    }
    
    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{mpaaRating} вывести элементы, значение поля mpaaRating которых больше заданного";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {  
        String arg = (String) request.getCommandArg();
        if(arg == null || arg.isEmpty()){
            return new Response(false, "Не введен рейтинг", null);
        }

        MpaaRating target ;
        try{
            target = MpaaRating.valueOf(arg.strip());
        }
        catch(IllegalArgumentException e){
            return new Response(false, "Некоректный рейтинг. Доступны только: " + MpaaRating.names(), null);
        }
        HashSet<Movie> collection = cm.getCollection();
        String movies = collection.stream().filter(movie -> movie.getMpaaRating().compareTo(target) > 0).sorted(Comparator.comparing(Movie::getName)).map(Movie::toString).collect(Collectors.joining("\n"));
        if(movies.isEmpty()){
            return new Response(true, "Фильмы не найдены", movies);
        }
        return new Response(true, "Элементы рейтинг которых больше заданного", movies);
       
    }
    
}