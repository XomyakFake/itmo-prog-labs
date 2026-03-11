package proglab5.commands;

public interface Command {

    /**
     * Исполнение команды
     * @param args Аргументы необходимые команде.
     * @return Успешность выполнения программы
     */
    void execute(String ... args);

    /**
     * @return Название команды
     */
    String getName();

    /**
     * @return Описание команды.
     */
    String getDescription();
}
