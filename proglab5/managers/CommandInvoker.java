package proglab5.managers;

import proglab5.commands.Command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Управляет командами
 * @author XomyakFake
 */
public class CommandInvoker {
    private final Map<String, Command> commandMap = new HashMap<>();
    private final Deque<String> history = new ArrayDeque<>();
    
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
        addToHisory(name, arg);
        command.execute(arg);
    }

    /**
     * Добавляет выполненную команду в историю
     * Если размер истории превышает 8 то самая старая команда удаляется
     * @param command Имя выполненной команды
     * @param arg Аргументы команды
     */
    public void addToHisory(String command, String arg){
        String full = arg.isEmpty() ? command : command + " " + arg;
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
        if(history.isEmpty()){
            System.out.println("История пуста");
        }
        return new ArrayList<>(history);
    }

}

