package com.aep.redeSaber.services;

import com.aep.redeSaber.exceptions.ResourceNotFoundException;
import com.aep.redeSaber.models.Avaliacao;
import com.aep.redeSaber.models.Material;
import com.aep.redeSaber.models.User;
import com.aep.redeSaber.repositories.AvaliacaoRepository;
import com.aep.redeSaber.repositories.MaterialRepository;
import com.aep.redeSaber.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MaterialRepository materialRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Test
    void deveCriarAvaliacaoComSucesso() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(5);
        avaliacao.setUsuarioId("user1");
        avaliacao.setMaterialId("mat1");

        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(materialRepository.findById("mat1")).thenReturn(Optional.of(new Material()));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        Avaliacao resultado = avaliacaoService.criar(avaliacao);

        assertThat(resultado.getNota()).isEqualTo(5);
        verify(userRepository).findById("user1");
        verify(materialRepository).findById("mat1");
        verify(avaliacaoRepository).save(avaliacao);
    }

    @Test
    void deveLancarExcecaoAoCriarAvaliacaoComUsuarioInexistente() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuarioId("inexistente");
        avaliacao.setMaterialId("mat1");

        when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            avaliacaoService.criar(avaliacao);
        });

        verify(userRepository).findById("inexistente");
        verify(materialRepository, never()).findById(any());
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCriarAvaliacaoComMaterialInexistente() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuarioId("user1");
        avaliacao.setMaterialId("inexistente");

        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(materialRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            avaliacaoService.criar(avaliacao);
        });

        verify(materialRepository).findById("inexistente");
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deveListarTodasAvaliacoes() {
        when(avaliacaoRepository.findAll()).thenReturn(List.of(new Avaliacao(), new Avaliacao()));

        List<Avaliacao> resultado = avaliacaoService.listar();

        assertThat(resultado).hasSize(2);
        verify(avaliacaoRepository).findAll();
    }

    @Test
    void deveListarAvaliacaoPorId() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("av1");

        when(avaliacaoRepository.findById("av1")).thenReturn(Optional.of(avaliacao));

        Optional<Avaliacao> resultado = avaliacaoService.listarPorId("av1");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo("av1");
    }

    @Test
    void deveListarAvaliacoesPorMaterial() {
        when(avaliacaoRepository.findByMaterialId("mat1")).thenReturn(List.of(new Avaliacao()));

        List<Avaliacao> resultado = avaliacaoService.listarPorMaterial("mat1");

        assertThat(resultado).hasSize(1);
        verify(avaliacaoRepository).findByMaterialId("mat1");
    }

    @Test
    void deveAtualizarAvaliacaoComSucesso() {
        Avaliacao existente = new Avaliacao();
        existente.setId("av1");
        existente.setNota(3);

        Avaliacao novosDados = new Avaliacao();
        novosDados.setNota(5);
        novosDados.setComentarioGeral("Excelente!");

        when(avaliacaoRepository.findById("av1")).thenReturn(Optional.of(existente));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(existente);

        Avaliacao resultado = avaliacaoService.atualizar(novosDados, "av1");

        assertThat(resultado.getNota()).isEqualTo(5);
        assertThat(resultado.getComentarioGeral()).isEqualTo("Excelente!");
        verify(avaliacaoRepository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarAvaliacaoInexistente() {
        when(avaliacaoRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            avaliacaoService.atualizar(new Avaliacao(), "999");
        });
    }

    @Test
    void deveDeletarAvaliacaoComSucesso() {
        when(avaliacaoRepository.existsById("av1")).thenReturn(true);
        doNothing().when(avaliacaoRepository).deleteById("av1");

        avaliacaoService.deletar("av1");

        verify(avaliacaoRepository).existsById("av1");
        verify(avaliacaoRepository).deleteById("av1");
    }

    @Test
    void deveLancarExcecaoAoDeletarAvaliacaoInexistente() {
        when(avaliacaoRepository.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            avaliacaoService.deletar("999");
        });
    }
}