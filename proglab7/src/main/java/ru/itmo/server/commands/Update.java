package ru.itmo.server.commands;

import ru.itmo.common.exceptions.ValidateException;
import ru.itmo.common.models.*;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.modules.CollectionManager;
import ru.itmo.server.modules.DatabaseManager;

/**
 * Команда 'update'. Обновляет элемент коллекции по его id
 * @author XomyakFake
 */
public class Update implements Command {
    private final CollectionManager cm;
    private final DatabaseManager dm;

    public Update(CollectionManager cm, DatabaseManager dm){
        this.cm = cm;
        this.dm = dm;
    }

    @Override 
    public String getName(){
        return "update";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{element} обновить значение элемента коллекции, id которого равен заданному";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request){
        try {
            Movie NewMovie = (Movie) request.getCommandArg();
            Integer target = NewMovie.getId(); 

            if (target == null) {
                return new Response(false, "Ошибка. Пустой ID.", null);
            }

            boolean removed = cm.getCollection().removeIf(m -> m.getId().equals(target));
            if (!removed) {
                return new Response(false, "Фильм с id=" + target + " не существует.", null);
            }
            cm.addMovie(NewMovie); 
            dm.update(NewMovie, request.getUser().getUsername());

            return new Response(true, "Фильм с id=" + target + " обновлен.", null);

        } catch (ValidateException e) {
            return new Response(false, "Ошибка валидации при обновлении", null);
        } catch (ClassCastException e) {
            return new Response(false, "Ошибка. Передан аргумент неправильного типа", null);
        }
    }
    
}
