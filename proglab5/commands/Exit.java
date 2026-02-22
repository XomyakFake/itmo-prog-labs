package proglab5.commands;

public class Exit implements Command {

    @Override 
    public String getName(){
        return "exit";
    }

    @Override
    public void execute(String... args){
        System.out.println("Завершение программы");
        System.exit(0);

    }

}
