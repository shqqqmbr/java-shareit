package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, int userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Integer userId, @Nullable Map<String, Object> parameters, Integer bookingId) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null, bookingId);
    }

    protected ResponseEntity<Object> get(String path, Integer userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, int userId, T body) {
        return post(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, Integer userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body, null);
    }

    protected <T> ResponseEntity<Object> put(String path, int userId, T body) {
        return put(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, int userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, int userId) {
        return patch(path, userId, null, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, int userId, T body) {
        return patch(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Integer userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, Integer userId, @Nullable Map<String, Object> parameters, T body, Integer bookingId) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body, bookingId);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    protected ResponseEntity<Object> delete(String path, int userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path, Integer userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                          Integer userId, @Nullable Map<String, Object> parameters,
                                                          @Nullable T body, Object... uriVariables) {
        String finalPath = path;
        if (uriVariables != null && uriVariables.length > 0) {
            finalPath = UriComponentsBuilder.fromPath(path)
                    .buildAndExpand(uriVariables)
                    .toUriString();
        }
        if (parameters != null && !parameters.isEmpty()) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(finalPath);
            parameters.forEach(builder::queryParam);
            finalPath = builder.toUriString();
        }
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        try {
            ResponseEntity<Object> response = rest.exchange(finalPath, method, requestEntity, Object.class);
            return prepareGatewayResponse(response);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders defaultHeaders(Integer userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
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