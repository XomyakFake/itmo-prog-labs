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
            Movie newMovie = (Movie) request.getCommandArg();
            Integer target = newMovie.getId(); 

            if (target == null) {
                return new Response(false, "Ошибка. Пустой ID.", null);
            }

            Movie found = cm.getCollection().stream().filter(m -> m.getId().equals(target)).findFirst().orElse(null);

            if (found == null) {
                return new Response(false, "Фильм не найден", null);
            }

            boolean updated = dm.update(newMovie, request.getUser().getUsername());
            if (!updated) {
                return new Response(false, "Нет прав или фильм не найден", null);
            }

            cm.getCollection().removeIf(m -> m.getId().equals(target));
            newMovie.setOwner(found.getOwner());
            cm.addMovie(newMovie);

            return new Response(true, "Фильм с id=" + target + " обновлен.", null);

        } catch (ValidateException e) {
            return new Response(false, "Ошибка валидации при обновлении", null);
        } catch (ClassCastException e) {
            return new Response(false, "Ошибка. Передан аргумент неправильного типа", null);
        }
    }
    
}
