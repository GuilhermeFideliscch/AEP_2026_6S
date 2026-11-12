package com.aep.redeSaber.controllers;

import com.aep.redeSaber.models.User;
import com.aep.redeSaber.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import com.aep.redeSaber.security.SecurityConfig;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void deveCadastrarUsuario() throws Exception {
        User user = new User();
        user.setId("usuario-1");
        user.setNome("Usuário");
        user.setEmail("usuario@email.com");

        when(userService.criar(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Usuário\",\"email\":\"usuario@email.com\",\"senha\":\"123456\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("usuario-1"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveRealizarLoginEGuardarIdNaSessao() throws Exception {
        User user = new User();
        user.setId("usuario-1");
        user.setEmail("usuario@email.com");
        user.setSenha("123456");
        when(userService.autenticar("usuario@email.com", "123456")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"usuario@email.com\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("usuarioId", "usuario-1"))
                .andExpect(jsonPath("$.id").value("usuario-1"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void deveRecusarLoginComCredenciaisInvalidas() throws Exception {
        when(userService.autenticar("usuario@email.com", "errada")).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"usuario@email.com\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveEncerrarSessaoNoLogout() throws Exception {
        mockMvc.perform(post("/users/logout").sessionAttr("usuarioId", "usuario-1"))
                .andExpect(status().isNoContent())
                .andExpect(request().sessionAttributeDoesNotExist("usuarioId"));
    }

    @Test
    void deveListarUsuarios() throws Exception {
        when(userService.listar()).thenReturn(List.of(new User(), new User()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        User user = new User();
        user.setId("usuario-1");
        when(userService.listarPorId("usuario-1")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/usuario-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("usuario-1"));
    }

    @Test
    void deveRetornarNotFoundParaUsuarioInexistente() throws Exception {
        when(userService.listarPorId("inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarUsuarioExistente() throws Exception {
        User entrada = new User();
        entrada.setNome("Nome atualizado");

        User atualizado = new User();
        atualizado.setId("usuario-1");
        atualizado.setNome("Nome atualizado");

        when(userService.listarPorId("usuario-1")).thenReturn(Optional.of(new User()));
        when(userService.atualizar(any(User.class), eq("usuario-1"))).thenReturn(atualizado);

        mockMvc.perform(put("/users/usuario-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome atualizado"));
    }

    @Test
    void deveRetornarNotFoundAoAtualizarUsuarioInexistente() throws Exception {
        when(userService.listarPorId("inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());

        verify(userService, never()).atualizar(any(User.class), anyString());
    }

    @Test
    void deveDeletarUsuarioExistente() throws Exception {
        when(userService.listarPorId("usuario-1")).thenReturn(Optional.of(new User()));
        doNothing().when(userService).deletar("usuario-1");

        mockMvc.perform(delete("/users/usuario-1"))
                .andExpect(status().isNoContent());

        verify(userService).deletar("usuario-1");
    }

    @Test
    void deveRetornarNotFoundAoDeletarUsuarioInexistente() throws Exception {
        when(userService.listarPorId("inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/inexistente"))
                .andExpect(status().isNotFound());

        verify(userService, never()).deletar(anyString());
    }
}
