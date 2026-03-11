package proglab5.commands;

/**
 * Команда 'exit'. Завершает выполнение программы
 * @author XomyakFake
 */
public class Exit implements Command {

    @Override 
    public String getName(){
        return "exit";
    }

    /**
     * Описание команды
     */
    @Override
    public String getDescription(){
        return "завершить программу (без сохранения в файл)";
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public void execute(String... args){
        System.out.println("Завершение программы");
        System.exit(0);

    }

}
