package ru.itmo.server.java.commands;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;

public interface Command {

    /**
     * Исполнение команды
     * @param args Аргументы необходимые команде.
     * @return Успешность выполнения программы
     */
    Response execute(Request request);

    /**
     * @return Название команды
     */
    String getName();

    /**
     * @return Описание команды.
     */
    String getDescription();
}
