package com.stage.appToDo;

import java.time.LocalDate;

public record TaskDto(
        Long id,
        String title,
        LocalDate dueDate,
        Task.Priority priority,
        Task.Status status,
        CategoryDto category
) {

    public static TaskDto fromEntity(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus(),
                CategoryDto.fromEntity(task.getCategory())
        );
    }
}
