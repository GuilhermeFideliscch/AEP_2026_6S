package com.aep.redeSaber.services;

import com.aep.redeSaber.exceptions.ResourceNotFoundException;
import com.aep.redeSaber.models.User;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void deveCriarUsuarioComSucessoEDefinirDataCriacao() {
        User user = new User();
        user.setNome("Guilherme");
        user.setEmail("guilherme@email.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User resultado = userService.criar(user);

        assertThat(resultado.getNome()).isEqualTo("Guilherme");
        assertThat(resultado.getDataCriacao()).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    void deveListarTodosUsuarios() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> resultado = userService.listar();

        assertThat(resultado).hasSize(2);
        verify(userRepository).findAll();
    }

    @Test
    void deveListarUsuarioPorId() {
        User user = new User();
        user.setId("1");

        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        Optional<User> resultado = userService.listarPorId("1");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo("1");
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        User existente = new User();
        existente.setId("1");
        existente.setNome("Nome Antigo");

        User dadosNovos = new User();
        dadosNovos.setNome("Nome Novo");
        dadosNovos.setEmail("novo@email.com");
        dadosNovos.setTelefone("99999999");
        dadosNovos.setSenha("123456");

        when(userRepository.findById("1")).thenReturn(Optional.of(existente));
        when(userRepository.save(any(User.class))).thenReturn(existente);

        User resultado = userService.atualizar(dadosNovos, "1");

        assertThat(resultado.getNome()).isEqualTo("Nome Novo");
        verify(userRepository).findById("1");
        verify(userRepository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        when(userRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.atualizar(new User(), "99");
        });
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        doNothing().when(userRepository).deleteById("1");

        userService.deletar("1");

        verify(userRepository).deleteById("1");
    }
}