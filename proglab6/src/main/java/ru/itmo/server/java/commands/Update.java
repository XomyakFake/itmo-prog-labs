package ru.itmo.server.java.commands;

import ru.itmo.common.exceptions.ValidateException;
import ru.itmo.common.models.*;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.java.modules.CollectionManager;

/**
 * Команда 'update'. Обновляет элемент коллекции по его id
 * @author XomyakFake
 */
public class Update implements Command {
    private final CollectionManager cm;

    public Update(CollectionManager cm){
        this.cm = cm;
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
            // Достаем фильм
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

            return new Response(true, "Фильм с id=" + target + " обновлен.", null);

        } catch (ValidateException e) {
            return new Response(false, "Ошибка валидации при обновлении", null);
        } catch (ClassCastException e) {
            return new Response(false, "Ошибка. Передан аргумент неправильного типа", null);
        }
    }
    
}
