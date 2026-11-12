package com.aep.redeSaber.controllers;

import com.aep.redeSaber.models.Avaliacao;
import com.aep.redeSaber.services.AvaliacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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

@WebMvcTest(AvaliacaoController.class)
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AvaliacaoService avaliacaoService;

    @Test
    @WithMockUser
    void deveSalvarAvaliacaoComSucesso() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");
        avaliacao.setNota(5);

        when(avaliacaoService.criar(any(Avaliacao.class))).thenReturn(avaliacao);

        mockMvc.perform(post("/avaliacoes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(avaliacao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.nota").value(5));
    }

    @Test
    @WithMockUser
    void deveListarTodasAvaliacoes() throws Exception {
        when(avaliacaoService.listar()).thenReturn(List.of(new Avaliacao(), new Avaliacao()));

        mockMvc.perform(get("/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void deveBuscarAvaliacaoPorId() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");

        when(avaliacaoService.listarPorId("1")).thenReturn(Optional.of(avaliacao));

        mockMvc.perform(get("/avaliacoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    @WithMockUser
    void deveRetornarNotFoundQuandoAvaliacaoNaoExistirPorId() throws Exception {
        when(avaliacaoService.listarPorId("99")).thenReturn(Optional.empty());

        mockMvc.perform(get("/avaliacoes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deveListarAvaliacoesPorMaterial() throws Exception {
        when(avaliacaoService.listarPorMaterial("mat1")).thenReturn(List.of(new Avaliacao()));

        mockMvc.perform(get("/avaliacoes/material/mat1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void deveAtualizarAvaliacao() throws Exception {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");
        avaliacao.setNota(4);

        when(avaliacaoService.atualizar(any(Avaliacao.class), eq("1"))).thenReturn(avaliacao);

        mockMvc.perform(put("/avaliacoes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(avaliacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota").value(4));
    }

    @Test
    @WithMockUser
    void deveDeletarAvaliacao() throws Exception {
        doNothing().when(avaliacaoService).deletar("1");

        mockMvc.perform(delete("/avaliacoes/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}