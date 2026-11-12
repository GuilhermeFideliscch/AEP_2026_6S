package com.aep.redeSaber.services;

import com.aep.redeSaber.exceptions.ResourceNotFoundException;
import com.aep.redeSaber.models.Material;
import com.aep.redeSaber.models.User;
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
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MaterialService materialService;

    @Test
    void deveCriarMaterialComSucesso() {
        Material material = new Material();
        material.setTitulo("Apostila NoSQL");
        material.setUsuarioId("user123");

        User user = new User();
        user.setId("user123");

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(materialRepository.save(any(Material.class))).thenReturn(material);

        Material resultado = materialService.criar(material);

        assertThat(resultado.getTitulo()).isEqualTo("Apostila NoSQL");
        verify(userRepository).findById("user123");
        verify(materialRepository).save(material);
    }

    @Test
    void deveLancarExcecaoAoCriarMaterialComUsuarioInexistente() {
        Material material = new Material();
        material.setUsuarioId("inexistente");

        when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            materialService.criar(material);
        });

        verify(userRepository).findById("inexistente");
        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void deveListarTodosMateriais() {
        when(materialRepository.findAll()).thenReturn(List.of(new Material(), new Material()));

        List<Material> resultado = materialService.listar();

        assertThat(resultado).hasSize(2);
        verify(materialRepository).findAll();
    }

    @Test
    void deveListarMaterialPorId() {
        Material material = new Material();
        material.setId("mat123");

        when(materialRepository.findById("mat123")).thenReturn(Optional.of(material));

        Optional<Material> resultado = materialService.listarPorId("mat123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo("mat123");
    }

    @Test
    void deveListarMateriaisPorUsuario() {
        when(materialRepository.findByUsuarioId("user123")).thenReturn(List.of(new Material()));

        List<Material> resultado = materialService.listarPorUsuario("user123");

        assertThat(resultado).hasSize(1);
        verify(materialRepository).findByUsuarioId("user123");
    }

    @Test
    void deveAtualizarMaterialComSucesso() {
        Material existente = new Material();
        existente.setId("mat123");
        existente.setTitulo("Antigo");

        Material dadosNovos = new Material();
        dadosNovos.setTitulo("Novo Título");
        dadosNovos.setDescricao("Nova Descrição");

        when(materialRepository.findById("mat123")).thenReturn(Optional.of(existente));
        when(materialRepository.save(any(Material.class))).thenReturn(existente);

        Material resultado = materialService.atualizar(dadosNovos, "mat123");

        assertThat(resultado.getTitulo()).isEqualTo("Novo Título");
        verify(materialRepository).findById("mat123");
        verify(materialRepository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarMaterialInexistente() {
        when(materialRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            materialService.atualizar(new Material(), "999");
        });
    }

    @Test
    void deveDeletarMaterialComSucesso() {
        when(materialRepository.existsById("mat123")).thenReturn(true);
        doNothing().when(materialRepository).deleteById("mat123");

        materialService.deletar("mat123");

        verify(materialRepository).existsById("mat123");
        verify(materialRepository).deleteById("mat123");
    }

    @Test
    void deveLancarExcecaoAoDeletarMaterialInexistente() {
        when(materialRepository.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            materialService.deletar("999");
        });
    }
}