db = db.getSiblingDB("User");

const validatorUser = {
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
_id: { bsonType: "string" },
nome: { bsonType: "string" },
email: { bsonType: "string" },
senha: { bsonType: "string" },
telefone: { bsonType: "string" },
ativo: { bsonType: "bool" },
dataCriacao: { bsonType: "date" },
enderecos: {
bsonType: "array",
items: {
bsonType: "object",
required: ["rua", "cidade", "estado"],
properties: {
rua: { bsonType: "string" },
cidade: { bsonType: "string" },
estado: { bsonType: "string" }
        }
        }
        }
        }
        }
        };

        if (!db.getCollectionNames().includes("users")) {
        db.createCollection("users", {
    validator: validatorUser,
            validationLevel: "strict",
            validationAction: "error"
});
        } else {
        db.runCommand({
    collMod: "users",
            validator: validatorUser,
            validationLevel: "strict",
            validationAction: "error"
});
        }