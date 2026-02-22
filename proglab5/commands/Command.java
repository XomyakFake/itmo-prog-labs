package proglab5.commands;

public interface Command {
    void execute(String ... args);
    String getName();
}
