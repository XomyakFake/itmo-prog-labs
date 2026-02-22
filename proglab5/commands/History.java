package proglab5.commands;

import java.util.List;

import proglab5.managers.CommandInvoker;

public class History implements Command {
    private final CommandInvoker commandinvoker;

    public History(CommandInvoker commandinvoker){
        this.commandinvoker = commandinvoker;
    }

    @Override 
    public String getName(){
        return "history";
    }

    @Override
    public void execute(String... args){
        List<String> his = commandinvoker.showHistory();
        for(String h : his){
            System.out.println(h);
        }
    }
    
}
