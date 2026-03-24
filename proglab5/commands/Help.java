package proglab5.commands;

import proglab5.managers.CommandInvoker;

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
    public void execute(String... args) {  
        for(Command command : commandInvoker.getCommands().values()){
            System.out.println(command.getName() + ": " + command.getDescription());
        }
       
    }
}