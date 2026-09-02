package com.aep.redeSaber.controllers;

import com.aep.redeSaber.models.User;
import com.aep.redeSaber.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    void deveCriarUsuarioComSucesso() throws Exception {
        User entrada = new User();
        entrada.setNome("Maria");
        entrada.setEmail("maria@teste.com");

        User salvo = new User();
        salvo.setId(1);
        salvo.setNome("Maria");
        salvo.setEmail("maria@teste.com");

        when(userService.criar(any(User.class))).thenReturn(salvo);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    @WithMockUser
    void deveListarTodosUsuarios() throws Exception {
        User user = new User();
        user.setId(1);
        user.setNome("Ana");

        when(userService.listar()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana"));
    }

    @Test
    @WithMockUser
    void deveListarVazioQuandoNaoHaUsuarios() throws Exception {
        when(userService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    void deveBuscarUsuarioPorIdQuandoExiste() throws Exception {
        User user = new User();
        user.setId(1);
        user.setNome("Bruno");

        when(userService.listarPorId(1)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Bruno"));
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        when(userService.listarPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deveAtualizarUsuarioComSucesso() throws Exception {
        Integer userId = 1;
        User entrada = new User();
        entrada.setNome("Nome Atualizado");
        entrada.setEmail("atualizado@teste.com");

        User salvo = new User();
        salvo.setId(userId);
        salvo.setNome("Nome Atualizado");
        salvo.setEmail("atualizado@teste.com");

        when(userService.listarPorId(userId)).thenReturn(Optional.of(new User()));
        when(userService.atualizar(any(User.class), eq(userId))).thenReturn(salvo);

        mockMvc.perform(put("/users/{id}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Atualizado"))
                .andExpect(jsonPath("$.email").value("atualizado@teste.com"));
    }

    @Test
    @WithMockUser
    void deveRetornar404AoAtualizarUsuarioInexistente() throws Exception {
        Integer userId = 99;
        User entrada = new User();
        entrada.setNome("Teste");

        when(userService.listarPorId(userId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/{id}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deveDeletarUsuarioComSucesso() throws Exception {
        Integer userId = 1;

        when(userService.listarPorId(userId)).thenReturn(Optional.of(new User()));
        doNothing().when(userService).deletar(userId);

        mockMvc.perform(delete("/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deveRetornar404AoDeletarUsuarioInexistente() throws Exception {
        Integer userId = 99;

        when(userService.listarPorId(userId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}