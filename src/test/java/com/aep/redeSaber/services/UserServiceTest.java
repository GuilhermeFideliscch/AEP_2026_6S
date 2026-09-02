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
}