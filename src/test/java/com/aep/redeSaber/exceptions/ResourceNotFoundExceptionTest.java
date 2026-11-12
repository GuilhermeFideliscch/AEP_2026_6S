package com.aep.redeSaber.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceNotFoundExceptionTest {

    @Test
    void deveArmazenarMensagemDaExcecao() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso não encontrado");

        assertEquals("Recurso não encontrado", exception.getMessage());
    }
}
