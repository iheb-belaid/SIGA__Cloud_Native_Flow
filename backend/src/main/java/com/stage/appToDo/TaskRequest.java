package com.stage.appToDo;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caracteres")
        String title,
        @FutureOrPresent(message = "La date limite doit etre aujourd'hui ou dans le futur")
        LocalDate dueDate,
        @NotNull(message = "La priorite est obligatoire")
        Task.Priority priority,
        @NotNull(message = "Le statut est obligatoire")
        Task.Status status,
        @NotNull(message = "La categorie est obligatoire")
        Long categoryId
) {
}
