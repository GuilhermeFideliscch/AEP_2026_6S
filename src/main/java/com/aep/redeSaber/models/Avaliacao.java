package com.aep.redeSaber.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "avaliacoes")
public class Avaliacao {

    @Id
    private String id;

    private Integer nota;
    private String comentarioGeral;

    private String usuarioId;
    private String materialId;

    private List<Criterio> criterios;

    @Data
    public static class Criterio {
        private String nome;
        private Integer peso;
    }
}