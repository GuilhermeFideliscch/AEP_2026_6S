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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void deveCriarUsuarioComDataDeCriacao() {
        User user = new User();
        user.setNome("Usuário");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = userService.criar(user);

        assertNotNull(resultado.getDataCriacao());
        assertEquals("Usuário", resultado.getNome());
        verify(userRepository).save(user);
    }

    @Test
    void deveListarTodosOsUsuarios() {
        List<User> usuarios = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(usuarios);

        List<User> resultado = userService.listar();

        assertEquals(2, resultado.size());
        assertSame(usuarios, resultado);
    }

    @Test
    void deveBuscarUsuarioPorId() {
        User user = new User();
        user.setId("usuario-1");
        when(userRepository.findById("usuario-1")).thenReturn(Optional.of(user));

        Optional<User> resultado = userService.listarPorId("usuario-1");

        assertTrue(resultado.isPresent());
        assertEquals("usuario-1", resultado.get().getId());
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoExistir() {
        when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertTrue(userService.listarPorId("inexistente").isEmpty());
    }

    @Test
    void deveAutenticarUsuarioComCredenciaisCorretas() {
        User user = new User();
        user.setEmail("usuario@email.com");
        user.setSenha("123456");
        when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));

        Optional<User> resultado = userService.autenticar("usuario@email.com", "123456");

        assertTrue(resultado.isPresent());
        assertSame(user, resultado.get());
    }

    @Test
    void deveRecusarSenhaIncorreta() {
        User user = new User();
        user.setEmail("usuario@email.com");
        user.setSenha("123456");
        when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));

        assertTrue(userService.autenticar("usuario@email.com", "senha-errada").isEmpty());
    }

    @Test
    void deveRecusarUsuarioComSenhaNula() {
        User user = new User();
        user.setEmail("usuario@email.com");
        user.setSenha(null);
        when(userRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(user));

        assertTrue(userService.autenticar("usuario@email.com", "123456").isEmpty());
    }

    @Test
    void deveRecusarEmailInexistente() {
        when(userRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertTrue(userService.autenticar("inexistente@email.com", "123456").isEmpty());
    }

    @Test
    void deveAtualizarUsuarioExistente() {
        User existente = new User();
        existente.setId("usuario-1");
        existente.setNome("Nome antigo");
        existente.setEmail("antigo@email.com");

        User dados = new User();
        dados.setNome("Nome novo");
        dados.setEmail("novo@email.com");
        dados.setTelefone("44999999999");
        dados.setSenha("nova-senha");

        when(userRepository.findById("usuario-1")).thenReturn(Optional.of(existente));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = userService.atualizar(dados, "usuario-1");

        assertEquals("Nome novo", resultado.getNome());
        assertEquals("novo@email.com", resultado.getEmail());
        assertEquals("44999999999", resultado.getTelefone());
        assertEquals("nova-senha", resultado.getSenha());
        verify(userRepository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.atualizar(new User(), "inexistente")
        );

        assertEquals("User não encontrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deveDeletarUsuario() {
        assertDoesNotThrow(() -> userService.deletar("usuario-1"));
        verify(userRepository).deleteById("usuario-1");
    }
}
