
package com.aep.redeSaber.controllers;

import com.aep.redeSaber.models.User;
import com.aep.redeSaber.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody User user) {
        userService.criar(user);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpSession session) {

        return userService.autenticar(request.email().trim(), request.senha())
                .map(user -> {
                    session.setAttribute("usuarioId", user.getId());

                    return ResponseEntity.ok(
                            new LoginResponse(
                                    user.getId(),
                                    user.getNome(),
                                    user.getEmail()
                            )
                    );
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> findALL() {
        return ResponseEntity.ok(userService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable String id) {
        return userService.listarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody User user) {
        if (userService.listarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.atualizar(user, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (userService.listarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    public record LoginRequest(String email, String senha) {
    }

    public record LoginResponse(String id, String nome, String email) {
    }
}
