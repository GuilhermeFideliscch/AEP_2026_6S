db = db.getSiblingDB("User");

const validatorMaterial = {
$jsonSchema: {
bsonType: "object",
required: [
        "titulo",
        "descricao",
        "linkArquivo",
        "usuarioId"
        ],
properties: {
_id: { bsonType: "string" },
titulo: { bsonType: "string" },
descricao: { bsonType: "string" },
linkArquivo: { bsonType: "string" },
usuarioId: { bsonType: "string" },
comentarios: {
bsonType: "array",
items: {
bsonType: "object",
required: ["autor", "texto"],
properties: {
autor: { bsonType: "string" },
texto: { bsonType: "string" }
        }
        }
        }
        }
        }
        };

        if (!db.getCollectionNames().includes("materiais")) {
        db.createCollection("materiais", {
    validator: validatorMaterial,
            validationLevel: "strict",
            validationAction: "error"
});
        } else {
        db.runCommand({
    collMod: "materiais",
            validator: validatorMaterial,
            validationLevel: "strict",
            validationAction: "error"
});
        }