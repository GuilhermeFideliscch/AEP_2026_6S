package com.aep.redeSaber.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "materiais")
public class Material {

    @Id
    private String id;

    private String titulo;
    private String descricao;
    private String linkArquivo;
    private String usuarioId;
    private List<Comentario> comentarios;

    @Data
    public static class Comentario {
        private String texto;
    }
}