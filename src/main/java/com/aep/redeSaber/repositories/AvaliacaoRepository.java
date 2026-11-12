package com.aep.redeSaber.repositories;

import com.aep.redeSaber.models.Avaliacao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends MongoRepository<Avaliacao, String> {
    List<Avaliacao> findByMaterialId(String materialId);
    List<Avaliacao> findByUsuarioId(String usuarioId);
}