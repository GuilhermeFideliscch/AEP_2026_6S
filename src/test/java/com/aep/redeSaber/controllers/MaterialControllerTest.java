
package com.aep.redeSaber.controllers;

import com.aep.redeSaber.models.Material;
import com.aep.redeSaber.services.MaterialService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaterialController.class)
@Import(SecurityConfig.class)
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaterialService materialService;

    @Test
    void deveCriarMaterialAssociadoAoUsuarioDaSessao() throws Exception {
        Material material = new Material();
        material.setId("material-1");
        material.setTitulo("Material de Matemática");
        material.setUsuarioId("usuario-1");

        when(materialService.criar(any(Material.class), eq("usuario-1"))).thenReturn(material);

        mockMvc.perform(post("/materiais")
                        .sessionAttr("usuarioId", "usuario-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Material de Matemática\",\"descricao\":\"Conteúdo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("material-1"))
                .andExpect(jsonPath("$.usuarioId").value("usuario-1"));

        verify(materialService).criar(any(Material.class), eq("usuario-1"));
    }

    @Test
    void deveRecusarCriacaoSemUsuarioNaSessao() throws Exception {
        mockMvc.perform(post("/materiais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Material\"}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(materialService);
    }

    @Test
    void deveListarTodosOsMateriais() throws Exception {
        when(materialService.listar()).thenReturn(List.of(new Material(), new Material()));

        mockMvc.perform(get("/materiais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deveBuscarMaterialPorId() throws Exception {
        Material material = new Material();
        material.setId("material-1");
        when(materialService.listarPorId("material-1")).thenReturn(Optional.of(material));

        mockMvc.perform(get("/materiais/material-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("material-1"));
    }

    @Test
    void deveRetornarNotFoundQuandoMaterialNaoExistir() throws Exception {
        when(materialService.listarPorId("inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(get("/materiais/inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarMateriaisDoUsuarioDaSessao() throws Exception {
        when(materialService.listarPorUsuario("usuario-1")).thenReturn(List.of(new Material()));

        mockMvc.perform(get("/materiais/usuario").sessionAttr("usuarioId", "usuario-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(materialService).listarPorUsuario("usuario-1");
    }

    @Test
    void deveRecusarListagemPorUsuarioSemSessao() throws Exception {
        mockMvc.perform(get("/materiais/usuario"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(materialService);
    }

    @Test
    void deveAtualizarMaterial() throws Exception {
        Material atualizado = new Material();
        atualizado.setId("material-1");
        atualizado.setTitulo("Título atualizado");
        atualizado.setUsuarioId("usuario-original");

        when(materialService.atualizar(any(Material.class), eq("material-1"))).thenReturn(atualizado);

        mockMvc.perform(put("/materiais/material-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Título atualizado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Título atualizado"))
                .andExpect(jsonPath("$.usuarioId").value("usuario-original"));
    }

    @Test
    void deveDeletarMaterial() throws Exception {
        doNothing().when(materialService).deletar("material-1");

        mockMvc.perform(delete("/materiais/material-1"))
                .andExpect(status().isNoContent());

        verify(materialService).deletar("material-1");
    }
}
