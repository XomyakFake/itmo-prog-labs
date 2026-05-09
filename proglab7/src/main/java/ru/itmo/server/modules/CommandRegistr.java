
package ru.itmo.server.modules;

import ru.itmo.server.commands.*;

public class CommandRegistr{
    public static void register(CommandInvoker invoker, CollectionManager collectionManager, DatabaseManager dm) {
        invoker.register(new Help(invoker));
        invoker.register(new Info(collectionManager));
        invoker.register(new Show(collectionManager));
        invoker.register(new Clear(dm, collectionManager));
        invoker.register(new Exit());
        invoker.register(new Add(collectionManager, dm));
        invoker.register(new RemoveById(collectionManager, dm));
        invoker.register(new History(invoker));
        invoker.register(new FilterGreaterThanMpaaRating(collectionManager));
        invoker.register(new PrintDescending(collectionManager));
        invoker.register(new PrintFieldDescendingTagline(collectionManager));
        invoker.register(new AddIfMax(collectionManager, dm));
        invoker.register(new RemoveGreater(collectionManager, dm));
        invoker.register(new Update(collectionManager, dm));
    }
}
