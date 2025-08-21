package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, 0, null);
    }

    protected ResponseEntity<Object> get(String path, String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get(path, 0, parameters);
    }

    protected ResponseEntity<Object> get(String path, Integer userId, Integer bookingId) {
        Map<String, Object> parameters = Map.of("userId", userId, "bookingId", bookingId);
        return get(path, userId, parameters);
    }

    protected ResponseEntity<Object> get(String path, int userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected ResponseEntity<Object> get(String path, Object uriVariable) {
        return makeAndSendRequest(HttpMethod.GET, path, 0, null, null, uriVariable);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, 0, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, int userId, T body) {
        return post(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, int userId, T body, Object... uriVariables) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, null, body, uriVariables);
    }

    protected <T> ResponseEntity<Object> post(String path, int userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, 0, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Object uriVariable, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, 0, null, body, uriVariable);
    }


    protected <T> ResponseEntity<Object> patch(String path, int userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }


    protected ResponseEntity<Object> delete(String path, Object uriVariable) {
        return makeAndSendRequest(HttpMethod.DELETE, path, 0, null, null, uriVariable);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Integer userId,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body, Object... uriVariables) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else if (uriVariables != null && uriVariables.length > 0) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, uriVariables);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, int userId,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        return makeAndSendRequest(method, path, userId, parameters, body, (Object[]) null);
    }

    private HttpHeaders defaultHeaders(int userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != 0) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}