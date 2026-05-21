package ru.itmo.server.modules;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;

public class Serializer {
    public static byte[] serialize(Response response) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();
        return baos.toByteArray();
    }

    public static Request deserialize(byte[] data) throws Exception{
        ObjectInputStream clientData = new ObjectInputStream(new ByteArrayInputStream(data));
        return (Request) clientData.readObject();
    }
    
}
