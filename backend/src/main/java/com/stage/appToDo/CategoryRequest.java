package com.stage.appToDo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Le nom de la categorie est obligatoire")
        @Size(max = 100, message = "Le nom de la categorie ne doit pas depasser 100 caracteres")
        String name
) {
}
