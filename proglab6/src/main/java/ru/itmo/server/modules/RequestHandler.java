package ru.itmo.server.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ru.itmo.common.network.Request;
import ru.itmo.common.network.Response;

public class RequestHandler {
    private final CommandInvoker commandInvoker;
    private final StorageCommands storage;
    private final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public RequestHandler(CommandInvoker commandInvoker, StorageCommands storage) {
        this.commandInvoker = commandInvoker;
        this.storage = storage;
    }

    public byte[] handle(byte[] data, String clientAddress) {
        try {
            Request request = Serializer.deserialize(data);
            String requestId = request.getRequestId().toString();

            if (storage.contains(requestId)) {
                logger.info("Повторная отправка ответа");
                return storage.get(requestId);
            }

            MDC.put("requestId", requestId);
            logger.info("Команда получена {} от {}", request.getCommandName(), clientAddress);

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