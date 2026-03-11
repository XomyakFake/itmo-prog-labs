package proglab5.commands;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.nio.file.Path;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

import proglab5.managers.CommandInvoker;

/**
 * Команда 'execute_script'. Исполняет скрипт из файла
 * @author XomyakFake
 */
public class ExecuteScript implements Command {
    private final CommandInvoker commandInvoker;
    private static final Set<String> executingScripts = new HashSet<>();
    public static boolean isExecuting = false;
    
    public ExecuteScript(CommandInvoker commandInvoker){
        this.commandInvoker = commandInvoker;
    }

    @Override 
    public String getName(){
        return "execute_script";
    }

    /**
     * Описание команды
    */
    @Override
    public String getDescription(){
        return "file_name считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public void execute(String... args) {

        if (args[0].isEmpty()) {
            System.out.println("Неправильный ввод аргумента");
            return;
        }

        String filename = args[0].strip();
        Path path = Path.of(filename);
        if(!Files.exists(path)){
            System.out.println("Такого файла нет");
            return;
        }
        if(!Files.isReadable(path)){
            System.out.println("Нет прав на чтение из файла");
            return;
        }

        String absolutePath = path.toAbsolutePath().normalize().toString();

        if(executingScripts.contains(absolutePath)){
            System.out.println("Обнаружена рекурсия. Пропуск выполнения");
            return;
        }

        executingScripts.add(absolutePath);
        isExecuting = true;
        try( 
            FileInputStream fis = new FileInputStream(absolutePath);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            Scanner filescanner = new Scanner(isr);
        ){
            while(filescanner.hasNextLine()){
                try{
                    String commandName = filescanner.nextLine().strip();
                    if(commandName.isEmpty()){
                        continue;
                    }
                    commandInvoker.execute(commandName);
                }
                catch(Exception e){
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
        catch(Exception e){
            System.out.println("Ошибка выполнения скрипта " + e.getMessage());
        }
        finally{
            executingScripts.remove(absolutePath);
            isExecuting = false;
        }
    }
}
