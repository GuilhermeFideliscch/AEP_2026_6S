package com.aep.redeSaber.services;

import com.aep.redeSaber.exceptions.ResourceNotFoundException;
import com.aep.redeSaber.models.Material;
import com.aep.redeSaber.repositories.MaterialRepository;
import com.aep.redeSaber.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;

    public MaterialService(MaterialRepository materialRepository, UserRepository userRepository) {
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
    }

    public Material criar(Material material, String usuarioId) {
        if (!userRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        material.setUsuarioId(usuarioId);

        return materialRepository.save(material);
    }

    public List<Material> listar() {
        return materialRepository.findAll();
    }

    public Optional<Material> listarPorId(String id) {
        return materialRepository.findById(id);
    }

    public List<Material> listarPorUsuario(String usuarioId) {
        return materialRepository.findByUsuarioId(usuarioId);
    }

    public Material atualizar(Material material, String id) {
        Material materialExistente = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado"));

        materialExistente.setTitulo(material.getTitulo());
        materialExistente.setDescricao(material.getDescricao());
        materialExistente.setLinkArquivo(material.getLinkArquivo());
        materialExistente.setComentarios(material.getComentarios());

        return materialRepository.save(materialExistente);
    }

    public void deletar(String id) {
        if (!materialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Material não encontrado");
        }

        materialRepository.deleteById(id);
    }
}