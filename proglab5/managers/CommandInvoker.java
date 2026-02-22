package proglab5.managers;

import proglab5.commands.Command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandInvoker {
    private final Map<String, Command> commandMap = new HashMap<>();
    private final Deque<String> history = new ArrayDeque<>();
    
    public void register(Command command){
        commandMap.put(command.getName(), command);
    }
    
    public void execute(String input){
        String parts[] = input.strip().split(" ", 2);
        String name = parts[0];
        String arg = "";
        if(parts.length > 1){
            arg = parts[1];
        }

        Command command = commandMap.get(name);
        if(command == null){
            System.out.println("Такой команды нет. Введите help чтобы вывести справку по существующим командам");
            return;
        }
        addToHisory(name);
        commandMap.get(name).execute(arg);
    }

    public void addToHisory(String command){
        history.addLast(command);
        if(history.size() > 8){
            history.removeFirst();
        }
    }

    public List<String> showHistory(){
    
        if(history.isEmpty()){
            System.out.println("История пуста");
        }
        return new ArrayList<>(history);
    }

}

