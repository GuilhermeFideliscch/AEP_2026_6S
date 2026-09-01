package com.aep.redeSaber.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "users")
public class User {

    @Id
    private Integer id;

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private boolean ativo;
    private Date dataCriacao;
}