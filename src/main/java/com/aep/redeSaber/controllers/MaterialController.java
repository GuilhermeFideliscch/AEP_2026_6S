package com.aep.redeSaber.controllers;

import com.aep.redeSaber.models.Material;
import com.aep.redeSaber.services.MaterialService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materiais")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    public ResponseEntity<Material> save(
            @RequestBody Material material,
            HttpSession session) {

        String usuarioId = (String) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.status(201)
                .body(materialService.criar(material, usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<Material>> findAll() {
        return ResponseEntity.ok(materialService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Material> findById(@PathVariable String id) {
        return materialService.listarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<Material>> findByUsuarioId(HttpSession session) {
        String usuarioId = (String) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(materialService.listarPorUsuario(usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Material> update(
            @PathVariable String id,
            @RequestBody Material material) {

        return ResponseEntity.ok(materialService.atualizar(material, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        materialService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}