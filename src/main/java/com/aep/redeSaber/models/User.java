package com.aep.redeSaber.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String nome;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    private String telefone;
    private boolean ativo;
    private Date dataCriacao;
    private List<Endereco> enderecos;

    @Data
    public static class Endereco {
        private String rua;
        private String cidade;
        private String estado;
    }
}