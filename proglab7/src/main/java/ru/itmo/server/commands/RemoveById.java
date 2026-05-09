package ru.itmo.server.commands;

import ru.itmo.common.models.Movie;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.modules.CollectionManager;
import ru.itmo.server.modules.DatabaseManager;

/**
 * Команда 'remove_by_id'. Удаляет элемент по его id
 * @author XomyakFake
 */
public class RemoveById implements Command {  
    private final CollectionManager cm;
    private final DatabaseManager dm;

    public RemoveById(CollectionManager cm, DatabaseManager dm){
        this.cm = cm;
        this.dm = dm;
    }

    @Override
    public String getName(){
        return "remove_by_id"; 
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "{id} удалить элемент из коллекции по его id";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {  
        try {
            String arg = (String) request.getCommandArg();
            int id = Integer.parseInt(arg.strip());

            Movie found = cm.getCollection().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);

            if (found == null) {
                return new Response(false, "Фильм не найден", null);
            }

            boolean removed = dm.removeById(id, request.getUser().getUsername());
            if (!removed) {
                return new Response(false, "Нет прав на удаление", null);
            }

            cm.getCollection().removeIf(m -> m.getId().equals(id));
            return new Response(true, "Фильм с id=" + id + " удален", null);

        } catch (NumberFormatException | NullPointerException e) {
            return new Response(false, "Некорректный id. Введите число", null);
        }
    }
}