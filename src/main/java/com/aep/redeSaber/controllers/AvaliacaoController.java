package com.aep.redeSaber.controllers;

import com.aep.redeSaber.models.Avaliacao;
import com.aep.redeSaber.services.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping
    public ResponseEntity<Avaliacao> save(@RequestBody Avaliacao avaliacao) {
        return ResponseEntity.status(201).body(avaliacaoService.criar(avaliacao));
    }

    @GetMapping
    public ResponseEntity<List<Avaliacao>> findAll() {
        return ResponseEntity.ok(avaliacaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> findById(@PathVariable String id) {
        return avaliacaoService.listarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<List<Avaliacao>> findByMaterialId(@PathVariable String materialId) {
        return ResponseEntity.ok(avaliacaoService.listarPorMaterial(materialId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> update(@PathVariable String id, @RequestBody Avaliacao avaliacao) {
        return ResponseEntity.ok(avaliacaoService.atualizar(avaliacao, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        avaliacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}