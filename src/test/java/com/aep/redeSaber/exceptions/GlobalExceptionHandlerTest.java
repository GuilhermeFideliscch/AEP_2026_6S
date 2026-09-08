package com.aep.redeSaber.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private WebRequest mockRequest(String uri) {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=" + uri);
        return request;
    }

    @Test
    void deveTratarResourceNotFoundException() {
        WebRequest request = mockRequest("/users/99");
        ResourceNotFoundException ex = new ResourceNotFoundException("User não encontrado");

        ResponseEntity<ApiError> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).isEqualTo("User não encontrado");
        assertThat(response.getBody().path()).isEqualTo("/users/99");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void deveTratarDuplicateKeyException() {
        WebRequest request = mockRequest("/users");
        DuplicateKeyException ex = new DuplicateKeyException("dup key");

        ResponseEntity<ApiError> response = handler.handleDuplicateKey(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().message()).contains("valor único");
    }

    @Test
    void deveTratarHttpMessageNotReadableException() {
        WebRequest request = mockRequest("/users");
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ApiError> response = handler.handleMalformedJson(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).isEqualTo("Corpo da requisição inválido ou mal formatado.");
    }

    @Test
    void deveTratarExcecaoGenerica() {
        WebRequest request = mockRequest("/users");
        Exception ex = new RuntimeException("qualquer erro inesperado");

        ResponseEntity<ApiError> response = handler.handleGeneric(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).isEqualTo("Erro interno inesperado.");
    }

    @Test
    void apiErrorDeveExporCamposCorretamenteEqualsEHashCode() {
        java.time.Instant now = java.time.Instant.now();
        ApiError a = new ApiError(now, 404, "Not Found", "msg", "/x");
        ApiError b = new ApiError(now, 404, "Not Found", "msg", "/x");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a.toString()).contains("Not Found");
    }
}