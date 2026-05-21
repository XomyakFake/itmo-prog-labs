package ru.itmo.server.commands;

import java.util.List;

import ru.itmo.common.network.*;
import ru.itmo.server.modules.CommandInvoker;

/**
 * Команда 'history'. Выводит последние 8 выполненных команд
 * @author XomyakFake
 */
public class History implements Command {
    private final CommandInvoker commandinvoker;

    public History(CommandInvoker commandinvoker){
        this.commandinvoker = commandinvoker;
    }

    @Override 
    public String getName(){
        return "history";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "вывести последние 8 команд";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request){
        List<String> his = commandinvoker.showHistory();
        String list = String.join("\n", his);
        return new Response(true, "История команд на сервере", list);
    }
    
}
