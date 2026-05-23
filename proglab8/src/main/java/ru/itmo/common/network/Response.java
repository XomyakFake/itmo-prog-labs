package ru.itmo.common.network;

import java.io.Serializable;
import java.util.UUID;

public class Response implements Serializable {
    private String message;
    private String collection;
    private boolean success;
    private UUID responseId;
    private String token;

    // 1. КРИТИЧЕСКИ ВАЖНО ДЛЯ JACKSON: Пустой конструктор
    public Response() {
    }

    // Твой оригинальный конструктор
    public Response(boolean success, String message, String collection) {
        this.success = success;
        this.message = message;
        this.collection = collection;
    }

    // Дополнительный удобный конструктор для простых ответов (ошибки/успех авторизации)
    public Response(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    // На всякий случай для Jackson, чтобы он точно сопоставил поле success
    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public UUID getResponseId() {
        return responseId;
    }

    public void setResponseId(UUID responseId) {
        this.responseId = responseId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}