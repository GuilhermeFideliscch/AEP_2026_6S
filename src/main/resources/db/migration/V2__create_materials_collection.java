
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
_id: {
bsonType: "string"
        },
titulo: {
bsonType: "string"
        },
descricao: {
bsonType: "string"
        },
linkArquivo: {
bsonType: "string"
        },
usuarioId: {
bsonType: "string"
        },
comentarios: {
bsonType: "array",
items: {
bsonType: "object",
required: ["texto"],
additionalProperties: false,
properties: {
texto: {
bsonType: "string"
        }
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

        db.materiais.updateMany(
{ comentarios: { $type: "array" } },
        { $unset: { "comentarios.$[].autor": "" } }
        );
