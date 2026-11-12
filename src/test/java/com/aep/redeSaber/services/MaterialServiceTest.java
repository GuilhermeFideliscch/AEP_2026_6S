package com.aep.redeSaber.services;

import com.aep.redeSaber.exceptions.ResourceNotFoundException;
import com.aep.redeSaber.models.Material;
import com.aep.redeSaber.repositories.MaterialRepository;
import com.aep.redeSaber.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MaterialService materialService;

    @Test
    void deveCriarMaterialAssociadoAoUsuario() {
        Material material = new Material();
        material.setTitulo("Matemática");
        material.setUsuarioId("usuario-enviado-pelo-cliente");

        when(userRepository.existsById("usuario-1")).thenReturn(true);
        when(materialRepository.save(any(Material.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Material resultado = materialService.criar(material, "usuario-1");

        assertEquals("usuario-1", resultado.getUsuarioId());
        assertEquals("Matemática", resultado.getTitulo());
        verify(userRepository).existsById("usuario-1");
        verify(materialRepository).save(material);
    }

    @Test
    void deveImpedirCriacaoQuandoUsuarioNaoExistir() {
        Material material = new Material();
        when(userRepository.existsById("usuario-inexistente")).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> materialService.criar(material, "usuario-inexistente")
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void deveListarTodosOsMateriais() {
        List<Material> materiais = List.of(new Material(), new Material());
        when(materialRepository.findAll()).thenReturn(materiais);

        List<Material> resultado = materialService.listar();

        assertEquals(2, resultado.size());
        assertSame(materiais, resultado);
    }

    @Test
    void deveBuscarMaterialPorId() {
        Material material = new Material();
        material.setId("material-1");
        when(materialRepository.findById("material-1")).thenReturn(Optional.of(material));

        Optional<Material> resultado = materialService.listarPorId("material-1");

        assertTrue(resultado.isPresent());
        assertEquals("material-1", resultado.get().getId());
    }

    @Test
    void deveRetornarVazioQuandoMaterialNaoExistir() {
        when(materialRepository.findById("inexistente")).thenReturn(Optional.empty());

        Optional<Material> resultado = materialService.listarPorId("inexistente");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveListarMateriaisDoUsuario() {
        List<Material> materiais = List.of(new Material());
        when(materialRepository.findByUsuarioId("usuario-1")).thenReturn(materiais);

        List<Material> resultado = materialService.listarPorUsuario("usuario-1");

        assertEquals(1, resultado.size());
        verify(materialRepository).findByUsuarioId("usuario-1");
    }

    @Test
    void deveAtualizarDadosSemAlterarUsuarioResponsavel() {
        Material existente = new Material();
        existente.setId("material-1");
        existente.setUsuarioId("usuario-original");
        existente.setTitulo("Título antigo");

        Material dados = new Material();
        dados.setTitulo("Título novo");
        dados.setDescricao("Descrição nova");
        dados.setLinkArquivo("https://exemplo.com/arquivo");

        Material.Comentario comentario = new Material.Comentario();
        comentario.setTexto("Comentário atualizado");
        dados.setComentarios(List.of(comentario));

        when(materialRepository.findById("material-1")).thenReturn(Optional.of(existente));
        when(materialRepository.save(any(Material.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Material resultado = materialService.atualizar(dados, "material-1");

        assertEquals("Título novo", resultado.getTitulo());
        assertEquals("Descrição nova", resultado.getDescricao());
        assertEquals("https://exemplo.com/arquivo", resultado.getLinkArquivo());
        assertEquals("usuario-original", resultado.getUsuarioId());
        assertEquals("Comentário atualizado", resultado.getComentarios().get(0).getTexto());
        verify(materialRepository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarMaterialInexistente() {
        when(materialRepository.findById("inexistente")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> materialService.atualizar(new Material(), "inexistente")
        );

        assertEquals("Material não encontrado", exception.getMessage());
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void deveDeletarMaterialExistente() {
        when(materialRepository.existsById("material-1")).thenReturn(true);

        assertDoesNotThrow(() -> materialService.deletar("material-1"));

        verify(materialRepository).deleteById("material-1");
    }

    @Test
    void deveLancarExcecaoAoDeletarMaterialInexistente() {
        when(materialRepository.existsById("inexistente")).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> materialService.deletar("inexistente")
        );

        assertEquals("Material não encontrado", exception.getMessage());
        verify(materialRepository, never()).deleteById(anyString());
    }
}
