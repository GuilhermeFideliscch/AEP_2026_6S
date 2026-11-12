package com.aep.redeSaber.repositories;

import com.aep.redeSaber.models.Material;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends MongoRepository<Material, String> {
    List<Material> findByUsuarioId(String usuarioId);
}