package ru.itmo.common.network;

import java.io.Serializable;
import java.util.UUID;

public class Response implements Serializable{
    private String message;
    private String collection;
    private boolean success;
    private UUID responseId;

    public Response(boolean success, String message, String collection){
        this.success = success;
        this.message = message;
        this.collection = collection;
    }

    public boolean isSuccess(){
        return success;
    }
    public String getMessage(){
        return message;
    }
    public String getCollection(){
        return collection;
    }
    public UUID getResponseId(){
        return responseId;
    }

    public void setResponseId(UUID responseId){
        this.responseId = responseId;
    }
    
}
