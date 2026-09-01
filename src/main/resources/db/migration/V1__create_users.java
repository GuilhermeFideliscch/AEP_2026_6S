// V1 - Criação/configuração da collection users
// Banco: User

db = db.getSiblingDB("User");

const validator = {
    $jsonSchema: {
    bsonType: "object",
    required: [
                "nome",
                "email",
                "senha",
                "telefone",
                "ativo",
                "dataCriacao"
                ],
            properties: {
            _id: {
            bsonType: "int"
                    },
            nome: {
            bsonType: "string"
                    },
            email: {
            bsonType: "string"
                    },
            senha: {
            bsonType: "string"
                    },
            telefone: {
            bsonType: "string"
                    },
            ativo: {
            bsonType: "bool"
                    },
            dataCriacao: {
            bsonType: "date"
            }
        }
    }
};

if (!db.getCollectionNames().includes("users")) {
    db.createCollection("users", {
            validator: validator,
            validationLevel: "strict",
            validationAction: "error"
    });
} else {
        db.runCommand({
            collMod: "users",
            validator: validator,
            validationLevel: "strict",
            validationAction: "error"
    });
}