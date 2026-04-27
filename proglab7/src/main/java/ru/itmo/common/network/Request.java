package ru.itmo.common.network;

import java.io.Serializable;
import java.util.UUID;

public class Request implements Serializable {
    private String commandName;
    private Serializable commandArg;
    private UUID requestId;

    public Request(String commandName, Serializable commandArg){
        this.requestId = UUID.randomUUID();
        this.commandArg = commandArg;
        this.commandName = commandName;

    }

    public UUID getRequestId(){
        return requestId;
    }
    public String getCommandName(){
        return commandName;
    }
    public Serializable getCommandArg(){
        return commandArg;
    }
    
}
