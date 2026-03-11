package proglab5.commands;

import java.util.List;
import proglab5.managers.CommandInvoker;

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
    public void execute(String... args){
        List<String> his = commandinvoker.showHistory();
        for(String h : his){
            System.out.println(h);
        }
    }
    
}
