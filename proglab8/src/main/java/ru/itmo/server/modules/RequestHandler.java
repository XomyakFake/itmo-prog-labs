package ru.itmo.server.modules;

import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ru.itmo.common.network.JsonConverter;
import ru.itmo.common.network.JwtToken;
import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;

public class RequestHandler {
    private final CommandInvoker commandInvoker;
    private final StorageCommands storage;
    private final DatabaseManager dm;
    private final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public RequestHandler(CommandInvoker commandInvoker, StorageCommands storage, DatabaseManager dm) {
        this.commandInvoker = commandInvoker;
        this.storage = storage;
        this.dm = dm;
    }

    public byte[] handle(byte[] data, String clientAddress) {
        String requestId = null;
        try {
            String json = new String(data, StandardCharsets.UTF_8).trim();
            Request request = JsonConverter.fromJson(json, Request.class);
            requestId = request.getRequestId().toString();

            if (storage.contains(requestId)) {
                logger.info("Повторная отправка ответа {}", requestId);
                return storage.get(requestId);
            }

            if (request.getCommandName() == null || request.getCommandName().isEmpty()) {
                Response response = handleAuth(request);
                return serialize(response, requestId);
            }

            MDC.put("requestId", requestId);
            logger.info("Команда {} от {}", request.getCommandName(), clientAddress);

            String userFromToken = JwtToken.validateToken(request.getToken());
            if (userFromToken == null || !userFromToken.equals(request.getUser().getUsername())) {
                Response response = new Response(false, "Сессия устарела. Войдите заново.", null);
                return serialize(response, requestId);
            }

            Response response = commandInvoker.execute(request);
            logger.info("Команда {} выполнена", request.getCommandName());
            return serialize(response, requestId);

        } catch (Exception e) {
            logger.error("Ошибка обработки запроса", e);
            try {
                Response err = new Response(false, "Внутренняя ошибка сервера: " + e.getMessage(), null);
                String errJson = JsonConverter.toJson(err);
                return errJson.getBytes(StandardCharsets.UTF_8);
            } catch (Exception ex) {
                return "{\"success\":false,\"message\":\"Fatal error\"}".getBytes(StandardCharsets.UTF_8);
            }
        } finally {
            MDC.clear();
        }
    }

    private Response handleAuth(Request request) {
        if (!request.isRegister()) {
            if (!dm.checkUserExistanse(request.getUser().getUsername())) {
                return new Response(false, "Пользователя " + request.getUser().getUsername() + " не существует", null);
            }
            if (!dm.checkUserPassword(request.getUser())) {
                return new Response(false, "Неверный пароль", null);
            }
            Response response = new Response(true, "Добро пожаловать!", null);
            response.setToken(JwtToken.generateToken(request.getUser().getUsername()));
            return response;
        } else {
            if (dm.checkUserExistanse(request.getUser().getUsername())) {
                return new Response(false, "Пользователь уже существует", null);
            }
            dm.addUser(request.getUser());
            Response response = new Response(true, "Пользователь зарегистрирован", null);
            response.setToken(JwtToken.generateToken(request.getUser().getUsername()));
            return response;
        }
    }

    private byte[] serialize(Response response, String requestId) throws Exception {
        if (requestId != null) response.setResponseId(java.util.UUID.fromString(requestId));
        byte[] bytes = JsonConverter.toJson(response).getBytes(StandardCharsets.UTF_8);
        if (requestId != null) storage.put(requestId, bytes);
        return bytes;
    }
}