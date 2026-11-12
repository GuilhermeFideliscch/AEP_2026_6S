db = db.getSiblingDB("User");

const validatorAvaliacao = {
$jsonSchema: {
bsonType: "object",
required: [
        "nota",
        "usuarioId",
        "materialId"
        ],
properties: {
_id: { bsonType: "string" },
nota: { bsonType: "int" },
comentarioGeral: { bsonType: "string" },
usuarioId: { bsonType: "string" },
materialId: { bsonType: "string" },
criterios: {
bsonType: "array",
description: "Lista de subdocumentos contendo os critérios da avaliação",
items: {
bsonType: "object",
required: ["nome", "peso"],
properties: {
nome: { bsonType: "string" },
peso: { bsonType: "int" }
        }
        }
        }
        }
        }
        };

        if (!db.getCollectionNames().includes("avaliacoes")) {
        db.createCollection("avaliacoes", {
    validator: validatorAvaliacao,
            validationLevel: "strict",
            validationAction: "error"
});
        } else {
        db.runCommand({
    collMod: "avaliacoes",
            validator: validatorAvaliacao,
            validationLevel: "strict",
            validationAction: "error"
});
        }