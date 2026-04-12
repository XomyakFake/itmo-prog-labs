package ru.itmo.server.java.modules;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.java.commands.Command;

/**
 * Управляет командами
 * @author XomyakFake
 */
public class CommandInvoker {
    private final Map<String, Command> commandMap = new HashMap<>();
    private final Deque<String> history = new ArrayDeque<>();
    private static Logger logger = LoggerFactory.getLogger(CommandInvoker.class);
    /**
     * Добавляет команду в менеджер.
     * @param command Команда.
     */
    public void register(Command command){
        commandMap.put(command.getName(), command);
    }
    
    /**
     * Разбирает введенную строку и исполняет ее
     * @param input Строка содержащая имя команды и ее аргументы
     */


    public Response execute(Request request){
        String name = request.getCommandName();
        Command command = commandMap.get(name);
        logger.info("Выполнение команды {}", name);
        if(command == null){
            logger.warn("Неизвестная команда {}", name);
            return new Response(false, "Такой команды нет", null);
        }
        addToHistory(name, request.getCommandArg());
        return command.execute(request);

    }


    /**
     * Добавляет выполненную команду в историю
     * Если размер истории превышает 8 то самая старая команда удаляется
     * @param command Имя выполненной команды
     * @param arg Аргументы команды
     */
    public void addToHistory(String command, Object arg){
        String full = (arg == null)? command : command + " " + arg;
        history.addLast(full);
        if(history.size() > 8){
            history.removeFirst();
        }
    }

    /**
     * Возвращает все существующие команды
     * @return Имя команды и объект команды
     */
    public Map<String, Command> getCommands(){
        return commandMap;
    }

    /**
     * Вовзращает историю последних выполненных команд
     * @return Последние выполненных команды
     */
    public List<String> showHistory(){
        return new ArrayList<>(history);
    }

}

