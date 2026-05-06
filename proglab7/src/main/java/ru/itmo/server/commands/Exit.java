package ru.itmo.server.commands;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.modules.CollectionManager;

/**
 * Команда 'exit'. Завершает выполнение программы
 * @author XomyakFake
 */
public class Exit implements Command {

    public Exit(){
    }
    
    @Override 
    public String getName(){
        return "exit";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "завершить программу (без сохранения в файл)";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request){
        System.exit(0);
        return new Response(true, "Завершение работы сервера и сохранение коллекции", null);

        

    }

}
