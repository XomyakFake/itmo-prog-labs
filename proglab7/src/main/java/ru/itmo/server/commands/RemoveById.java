package ru.itmo.server.commands;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.modules.CollectionManager;

/**
 * Команда 'remove_by_id'. Удаляет элемент по его id
 * @author XomyakFake
 */
public class RemoveById implements Command {  
    private final CollectionManager cm;

    public RemoveById(CollectionManager cm){
        this.cm = cm;
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

            boolean removed = cm.getCollection().removeIf(movie -> movie.getId().equals(id));

            if (removed) {
                return new Response(true, "Фильм с id=" + id + " удален", null);
            } else {
                return new Response(false, "Фильм с таким id не найден", null);
            }

        } catch (NumberFormatException | NullPointerException e) {
            return new Response(false, "Некорректный id. Введите число", null);
        }
    }
}