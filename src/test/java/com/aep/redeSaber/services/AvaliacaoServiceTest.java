
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
        avaliacao.setMaterialId("mat1");

        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(materialRepository.findById("mat1")).thenReturn(Optional.of(new Material()));
        when(avaliacaoRepository.save(any(Avaliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Avaliacao resultado = avaliacaoService.criar(avaliacao, "user1");

        assertThat(resultado.getNota()).isEqualTo(5);
        assertThat(resultado.getUsuarioId()).isEqualTo("user1");

        verify(userRepository).findById("user1");
        verify(materialRepository).findById("mat1");
        verify(avaliacaoRepository).save(avaliacao);
    }

    @Test
    void deveLancarExcecaoAoCriarAvaliacaoComUsuarioInexistente() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setMaterialId("mat1");

        when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> avaliacaoService.criar(avaliacao, "inexistente")
        );

        verify(userRepository).findById("inexistente");
        verify(materialRepository, never()).findById(any());
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCriarAvaliacaoComMaterialInexistente() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setMaterialId("inexistente");

        when(userRepository.findById("user1")).thenReturn(Optional.of(new User()));
        when(materialRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> avaliacaoService.criar(avaliacao, "user1")
        );

        verify(materialRepository).findById("inexistente");
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deveListarAvaliacoes() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");

        when(avaliacaoRepository.findAll()).thenReturn(List.of(avaliacao));

        List<Avaliacao> resultado = avaliacaoService.listar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo("1");
    }

    @Test
    void deveBuscarAvaliacaoPorId() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId("1");

        when(avaliacaoRepository.findById("1")).thenReturn(Optional.of(avaliacao));

        Optional<Avaliacao> resultado = avaliacaoService.listarPorId("1");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo("1");
    }

    @Test
    void deveListarAvaliacoesPorMaterial() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setMaterialId("mat1");

        when(avaliacaoRepository.findByMaterialId("mat1")).thenReturn(List.of(avaliacao));

        List<Avaliacao> resultado = avaliacaoService.listarPorMaterial("mat1");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMaterialId()).isEqualTo("mat1");
    }

    @Test
    void deveAtualizarAvaliacao() {
        Avaliacao existente = new Avaliacao();
        existente.setId("1");
        existente.setNota(3);

        Avaliacao atualizacao = new Avaliacao();
        atualizacao.setNota(5);
        atualizacao.setComentarioGeral("Atualizada");

        when(avaliacaoRepository.findById("1")).thenReturn(Optional.of(existente));
        when(avaliacaoRepository.save(any(Avaliacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Avaliacao resultado = avaliacaoService.atualizar(atualizacao, "1");

        assertThat(resultado.getNota()).isEqualTo(5);
        assertThat(resultado.getComentarioGeral()).isEqualTo("Atualizada");
        verify(avaliacaoRepository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarAvaliacaoInexistente() {
        Avaliacao atualizacao = new Avaliacao();

        when(avaliacaoRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> avaliacaoService.atualizar(atualizacao, "inexistente")
        );

        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deveDeletarAvaliacao() {
        when(avaliacaoRepository.existsById("1")).thenReturn(true);

        avaliacaoService.deletar("1");

        verify(avaliacaoRepository).deleteById("1");
    }

    @Test
    void deveLancarExcecaoAoDeletarAvaliacaoInexistente() {
        when(avaliacaoRepository.existsById("inexistente")).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> avaliacaoService.deletar("inexistente")
        );

        verify(avaliacaoRepository, never()).deleteById(any());
    }
}
