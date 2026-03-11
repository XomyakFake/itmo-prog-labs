package proglab5;

import java.util.NoSuchElementException;
import java.util.Scanner;
import proglab5.commands.*;
import proglab5.managers.CollectionManager;
import proglab5.managers.CommandInvoker;
import proglab5.managers.DumpManager;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        CommandInvoker commandinvoker = new CommandInvoker();
        DumpManager dumpManager = new DumpManager();
        CollectionManager collectionmanager = new CollectionManager(dumpManager);
        collectionmanager.loadCollection();

        commandinvoker.register(new Help(commandinvoker));
        commandinvoker.register(new Info(collectionmanager));
        commandinvoker.register(new Show(collectionmanager));
        commandinvoker.register(new Clear(collectionmanager));
        commandinvoker.register(new Exit());
        commandinvoker.register(new Add(scanner, collectionmanager));
        commandinvoker.register(new RemoveById(collectionmanager));
        commandinvoker.register(new History(commandinvoker));
        commandinvoker.register(new FilterGreaterThanMpaaRating(collectionmanager));
        commandinvoker.register(new PrintDescending(collectionmanager));
        commandinvoker.register(new PrintFieldDescendingTagline(collectionmanager));
        commandinvoker.register(new AddIfMax(scanner, collectionmanager));
        commandinvoker.register(new RemoveGreater(scanner, collectionmanager));
        commandinvoker.register(new Update(scanner, collectionmanager));
        commandinvoker.register(new Save(collectionmanager));
        commandinvoker.register(new ExecuteScript(commandinvoker));

        while(true){
            try{
                if(!scanner.hasNextLine()){
                    break;
                }
                String commandName = scanner.nextLine();
                commandinvoker.execute(commandName);
            }
            catch(NoSuchElementException e){
                System.out.println("\nВвод прерван. Завершение работы");
                break;
            }
            catch(Exception e){
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}


