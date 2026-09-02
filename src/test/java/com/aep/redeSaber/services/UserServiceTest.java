package com.aep.redeSaber.services;

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
    void deveCriarUsuario() {
        User user = new User();
        user.setNome("Guilherme");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User resultado = userService.criar(user);

        assertThat(resultado.getNome()).isEqualTo("Guilherme");
        verify(userRepository).save(user);
    }

    @Test
    void deveListarTodosUsuarios() {
        User user1 = new User();
        user1.setNome("Ana");
        User user2 = new User();
        user2.setNome("Bruno");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> resultado = userService.listar();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(User::getNome).containsExactly("Ana", "Bruno");
    }

    @Test
    void deveListarUsuarioPorIdQuandoExiste() {
        User user = new User();
        user.setId(1);
        user.setNome("Carla");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> resultado = userService.listarPorId(1);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Carla");
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoExiste() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        Optional<User> resultado = userService.listarPorId(99);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        User existente = new User();
        existente.setId(1);
        existente.setNome("Antigo");

        User dadosAtualizados = new User();
        dadosAtualizados.setNome("Novo Nome");
        dadosAtualizados.setEmail("novo@email.com");
        dadosAtualizados.setTelefone("123456789");
        dadosAtualizados.setSenha("123456");

        when(userRepository.findById(1)).thenReturn(Optional.of(existente));
        when(userRepository.save(any(User.class))).thenReturn(existente);

        User resultado = userService.atualizar(dadosAtualizados, 1);

        assertThat(resultado.getNome()).isEqualTo("Novo Nome");
        verify(userRepository).findById(1);
        verify(userRepository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            userService.atualizar(new User(), 99);
        });

        assertThat(excecao.getMessage()).isEqualTo("User não encontrado");
        verify(userRepository).findById(99);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deveDeletarUsuario() {
        doNothing().when(userRepository).deleteById(1);

        userService.deletar(1);

        verify(userRepository).deleteById(1);
    }
}