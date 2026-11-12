package com.aep.redeSaber.controllers;

import com.aep.redeSaber.models.Material;
import com.aep.redeSaber.services.MaterialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest; // <-- Mesmo import da sua classe funcional
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaterialController.class)
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private MaterialService materialService;

    @Test
    @WithMockUser
    void deveSalvarMaterialComSucesso() throws Exception {
        Material material = new Material();
        material.setId("1");
        material.setTitulo("Java NoSQL");

        when(materialService.criar(any(Material.class))).thenReturn(material);

        mockMvc.perform(post("/materiais")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(material)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.titulo").value("Java NoSQL"));
    }

    @Test
    @WithMockUser
    void deveListarTodosMateriais() throws Exception {
        when(materialService.listar()).thenReturn(List.of(new Material(), new Material()));

        mockMvc.perform(get("/materiais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void deveBuscarMaterialPorId() throws Exception {
        Material material = new Material();
        material.setId("1");

        when(materialService.listarPorId("1")).thenReturn(Optional.of(material));

        mockMvc.perform(get("/materiais/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    @WithMockUser
    void deveRetornarNotFoundQuandoMaterialNaoExistirPorId() throws Exception {
        when(materialService.listarPorId("99")).thenReturn(Optional.empty());

        mockMvc.perform(get("/materiais/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deveListarMateriaisPorUsuario() throws Exception {
        when(materialService.listarPorUsuario("user1")).thenReturn(List.of(new Material()));

        mockMvc.perform(get("/materiais/usuario/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void deveAtualizarMaterial() throws Exception {
        Material material = new Material();
        material.setId("1");
        material.setTitulo("Atualizado");

        when(materialService.atualizar(any(Material.class), eq("1"))).thenReturn(material);

        mockMvc.perform(put("/materiais/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(material)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Atualizado"));
    }

    @Test
    @WithMockUser
    void deveDeletarMaterial() throws Exception {
        doNothing().when(materialService).deletar("1");

        mockMvc.perform(delete("/materiais/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}