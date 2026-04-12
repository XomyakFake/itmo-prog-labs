package ru.itmo.server.java.commands;

import java.util.Collection;

import java.util.stream.Collectors;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;
import ru.itmo.server.java.modules.CommandInvoker;

/**
 * Команда 'help'. Выводит справку по программе
 * @author XomyakFake
 */
public class Help implements Command {  
    CommandInvoker commandInvoker;

    public Help(CommandInvoker commandInvoker){
        this.commandInvoker = commandInvoker;
    } 
    @Override
    public String getName(){
        return "help"; 
    }

    /**
     * Описание команды
    */
    @Override
    public String getDescription(){
        return "вывести справку по доступным командам";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {  
        Collection<Command> commands = commandInvoker.getCommands().values();
        String data = commands.stream().map(cmnd -> cmnd.getName() + ":" + cmnd.getDescription()).collect(Collectors.joining("\n"));
        return new Response(true, "Команды", data);
       
    }
}