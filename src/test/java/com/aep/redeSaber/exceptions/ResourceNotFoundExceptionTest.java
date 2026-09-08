package com.aep.redeSaber.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User não encontrado");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("User não encontrado");
    }
}