
package com.aep.redeSaber.controllers;

import com.aep.redeSaber.models.Avaliacao;
import com.aep.redeSaber.services.AvaliacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvaliacaoController.class)
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvaliacaoService avaliacaoService;

    @Test
    void deveSalvarAvaliacaoComSucesso() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");
        avaliacao.setNota(5);
        avaliacao.setMaterialId("mat1");

        when(avaliacaoService.criar(any(Avaliacao.class), eq("user1"))).thenReturn(avaliacao);

        mockMvc.perform(post("/avaliacoes")
                        .with(user("usuario"))
                        .with(csrf())
                        .sessionAttr("usuarioId", "user1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\":5,\"materialId\":\"mat1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.nota").value(5));

        verify(avaliacaoService).criar(any(Avaliacao.class), eq("user1"));
    }

    @Test
    void deveRetornarUnauthorizedQuandoNaoHouverUsuarioNaSessao() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(5);
        avaliacao.setMaterialId("mat1");

        mockMvc.perform(post("/avaliacoes")
                        .with(user("usuario"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\":5,\"materialId\":\"mat1\"}"))
                .andExpect(status().isUnauthorized());

        verify(avaliacaoService, never()).criar(any(Avaliacao.class), any());
    }

    @Test
    void deveListarTodasAvaliacoes() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");
        avaliacao.setNota(5);

        when(avaliacaoService.listar()).thenReturn(List.of(avaliacao));

        mockMvc.perform(get("/avaliacoes")
                        .with(user("usuario")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].nota").value(5));
    }

    @Test
    void deveBuscarAvaliacaoPorId() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");
        avaliacao.setNota(5);

        when(avaliacaoService.listarPorId("1")).thenReturn(Optional.of(avaliacao));

        mockMvc.perform(get("/avaliacoes/1")
                        .with(user("usuario")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void deveRetornarNotFoundQuandoAvaliacaoNaoExistir() throws Exception {
        when(avaliacaoService.listarPorId("inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(get("/avaliacoes/inexistente")
                        .with(user("usuario")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarAvaliacoesPorMaterial() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");
        avaliacao.setMaterialId("mat1");

        when(avaliacaoService.listarPorMaterial("mat1")).thenReturn(List.of(avaliacao));

        mockMvc.perform(get("/avaliacoes/material/mat1")
                        .with(user("usuario")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].materialId").value("mat1"));
    }

    @Test
    void deveAtualizarAvaliacao() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");
        avaliacao.setNota(4);
        avaliacao.setComentarioGeral("Boa avaliação");

        when(avaliacaoService.atualizar(any(Avaliacao.class), eq("1"))).thenReturn(avaliacao);

        mockMvc.perform(put("/avaliacoes/1")
                        .with(user("usuario"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nota\":4,\"comentarioGeral\":\"Boa avaliação\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota").value(4))
                .andExpect(jsonPath("$.comentarioGeral").value("Boa avaliação"));
    }

    @Test
    void deveDeletarAvaliacao() throws Exception {
        doNothing().when(avaliacaoService).deletar("1");

        mockMvc.perform(delete("/avaliacoes/1")
                        .with(user("usuario"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(avaliacaoService).deletar("1");
    }
}
