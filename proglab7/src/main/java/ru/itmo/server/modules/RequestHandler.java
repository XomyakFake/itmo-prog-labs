package ru.itmo.server.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ru.itmo.common.network.JwtToken;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;

public class RequestHandler {
    private final CommandInvoker commandInvoker;
    private final StorageCommands storage;
    public final DatabaseManager dm;
    private final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public RequestHandler(CommandInvoker commandInvoker, StorageCommands storage, DatabaseManager dm) {
        this.commandInvoker = commandInvoker;
        this.storage = storage;
        this.dm = dm;
    }

    public byte[] handle(byte[] data, String clientAddress) {
        try {
            Request request = Serializer.deserialize(data);
            String requestId = request.getRequestId().toString();

            if (storage.contains(requestId)) {
                logger.info("Повторная отправка ответа");
                return storage.get(requestId);
            }

            if(request.getCommandName() == null){
                Response response;
                if(!request.isRegister()){
                    if(dm.checkUserExistanse(request.getUser().getUsername()) && dm.checkUserPassword(request.getUser())){
                        response = new Response(true, "Добро пожаловать!", null);

                        String token = JwtToken.generateToken(request.getUser().getUsername());
                        response.setToken(token);
                    }
                    else if(!dm.checkUserExistanse(request.getUser().getUsername())){
                        response = new Response(false, "Пользователя " + request.getUser().getUsername() + " не существует", null);
                    }
                    else{
                        response = new Response(false, "Неверный пароль", null);
                    }

                } else {
                    if(dm.checkUserExistanse(request.getUser().getUsername())){
                        response = new Response(false, "Пользователь " + request.getUser().getUsername() + " уже существует", null);
                    }
                    else{
                        dm.addUser(request.getUser());
                        response = new Response(true, "Пользователь зарегестрирован", null);
                    }
                } 
                response.setResponseId(request.getRequestId());
                byte[] responseData = Serializer.serialize(response);
                storage.put(requestId, responseData);
                return responseData;
            }


            MDC.put("requestId", requestId);
            logger.info("Команда получена {} от {}", request.getCommandName(), clientAddress);

            String usernameFromToken = JwtToken.validateToken(request.getToken());

            if (usernameFromToken == null || !usernameFromToken.equals(request.getUser().getUsername())) {
                        Response response = new Response(false, "Сессия устарела. Требуется повторный вход.", null);
                        response.setResponseId(request.getRequestId());
                        byte[] responseData = Serializer.serialize(response);
                        storage.put(requestId, responseData);
                        return responseData;
            }

            Response response = commandInvoker.execute(request);
            response.setResponseId(request.getRequestId());
            logger.info("Команда {} выполнена", request.getCommandName());

            byte[] responseData = Serializer.serialize(response);
            storage.put(requestId, responseData);

            return responseData;

        } catch (Exception e) {
            logger.error("Ошибка при обработке запроса", e);
            return null;
        } finally {
            MDC.clear();
        }
    }
}