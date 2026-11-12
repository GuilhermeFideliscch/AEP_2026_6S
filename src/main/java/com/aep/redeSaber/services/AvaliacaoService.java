
package com.aep.redeSaber.services;

import com.aep.redeSaber.exceptions.ResourceNotFoundException;
import com.aep.redeSaber.models.Avaliacao;
import com.aep.redeSaber.repositories.AvaliacaoRepository;
import com.aep.redeSaber.repositories.MaterialRepository;
import com.aep.redeSaber.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            UserRepository userRepository,
                            MaterialRepository materialRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.userRepository = userRepository;
        this.materialRepository = materialRepository;
    }

    public Avaliacao criar(Avaliacao avaliacao, String usuarioId) {
        if (userRepository.findById(usuarioId).isEmpty()) {
            throw new ResourceNotFoundException("Usuário avaliador não encontrado");
        }

        if (avaliacao.getMaterialId() == null
                || materialRepository.findById(avaliacao.getMaterialId()).isEmpty()) {
            throw new ResourceNotFoundException("Material avaliado não encontrado");
        }

        avaliacao.setUsuarioId(usuarioId);

        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> listar() {
        return avaliacaoRepository.findAll();
    }

    public Optional<Avaliacao> listarPorId(String id) {
        return avaliacaoRepository.findById(id);
    }

    public List<Avaliacao> listarPorMaterial(String materialId) {
        return avaliacaoRepository.findByMaterialId(materialId);
    }

    public Avaliacao atualizar(Avaliacao avaliacao, String id) {
        Avaliacao existente = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));

        existente.setNota(avaliacao.getNota());
        existente.setComentarioGeral(avaliacao.getComentarioGeral());
        existente.setCriterios(avaliacao.getCriterios());

        return avaliacaoRepository.save(existente);
    }

    public void deletar(String id) {
        if (!avaliacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Avaliação não encontrada");
        }

        avaliacaoRepository.deleteById(id);
    }
}
