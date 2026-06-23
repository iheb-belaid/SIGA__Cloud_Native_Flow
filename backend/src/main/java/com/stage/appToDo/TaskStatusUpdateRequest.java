package com.stage.appToDo;

import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
        @NotNull(message = "Le statut est obligatoire")
        Task.Status status
) {
}
