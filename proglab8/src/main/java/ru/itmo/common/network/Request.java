package ru.itmo.common.network;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.UUID;

public class Request implements Serializable {
    private String commandName;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    private Object commandArg;

    private UUID requestId;
    private User user;
    private boolean isRegister;
    private String token;

    public Request() {}

    public Request(String commandName, Object commandArg, User user) {
        this.requestId = UUID.randomUUID();
        this.commandArg = commandArg;
        this.commandName = commandName;
        this.user = user;
    }

    public Request(User user, boolean isRegister) {
        this.requestId = UUID.randomUUID();
        this.user = user;
        this.isRegister = isRegister;
        this.commandName = "";
    }

    public Object getCommandArg() { return commandArg; }
    public void setCommandArg(Object commandArg) { this.commandArg = commandArg; }

    public UUID getRequestId() { return requestId; }
    public void setRequestId(UUID requestId) { this.requestId = requestId; }
    public String getCommandName() { return commandName; }
    public void setCommandName(String commandName) { this.commandName = commandName; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public boolean isRegister() { return isRegister; }
    public void setRegister(boolean isRegister) { this.isRegister = isRegister; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}