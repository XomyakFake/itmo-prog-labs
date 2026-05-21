package ru.itmo.common.network;

import java.io.Serializable;
import java.util.UUID;

public class Request implements Serializable {
    private String commandName;
    private Serializable commandArg;
    private UUID requestId;
    private User user;
    private boolean isRegister;
    private String token;

    public Request(String commandName, Serializable commandArg, User user) {
        this.requestId = UUID.randomUUID();
        this.commandArg = commandArg;
        this.commandName = commandName;
        this.user = user;

    }
    public Request(User user, boolean isRegister){
        this.requestId = UUID.randomUUID();
        this.user = user;
        this.isRegister = isRegister;
        this.commandName = null;
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
    public User getUser(){
        return user;
    }
    public boolean isRegister(){
        return isRegister;
    }
    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }
}
