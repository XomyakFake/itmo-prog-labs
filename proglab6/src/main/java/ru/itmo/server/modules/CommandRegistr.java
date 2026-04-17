
package ru.itmo.server.modules;

import ru.itmo.server.commands.*;

public class CommandRegistr{
    public static void register(CommandInvoker invoker, CollectionManager collectionManager) {
        invoker.register(new Help(invoker));
        invoker.register(new Info(collectionManager));
        invoker.register(new Show(collectionManager));
        invoker.register(new Clear(collectionManager));
        invoker.register(new Exit(collectionManager));
        invoker.register(new Add(collectionManager));
        invoker.register(new RemoveById(collectionManager));
        invoker.register(new History(invoker));
        invoker.register(new FilterGreaterThanMpaaRating(collectionManager));
        invoker.register(new PrintDescending(collectionManager));
        invoker.register(new PrintFieldDescendingTagline(collectionManager));
        invoker.register(new AddIfMax(collectionManager));
        invoker.register(new RemoveGreater(collectionManager));
        invoker.register(new Update(collectionManager));
        invoker.register(new Save(collectionManager));
    }
}
