package ru.itmo.server.modules;

import java.util.LinkedHashMap;
import java.util.Map;

public class StorageCommands {
    private Map<String, byte[]> reqIds = new LinkedHashMap<>();
    private final int maxSize = 100;

    public boolean contains(String requestId){
        return reqIds.containsKey(requestId);
    }

    public byte[] get(String requestId){
        return reqIds.get(requestId);
    }

    public void put(String requestId, byte[] responseData){
        if(reqIds.size() > maxSize){
            String old = reqIds.keySet().iterator().next();
            reqIds.remove(old);
        }
        reqIds.put(requestId, responseData);
    }
}
