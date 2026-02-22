package proglab5;
import java.util.Scanner;

import proglab5.commands.*;
import proglab5.managers.CollectionManager;
import proglab5.managers.CommandInvoker;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        CommandInvoker commandinvoker = new CommandInvoker();
        CollectionManager collectionmanager = new CollectionManager();

        commandinvoker.register(new Help());
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

        while(true){
            String commandName = scanner.nextLine();

            commandinvoker.execute(commandName);
        }

    }
}
