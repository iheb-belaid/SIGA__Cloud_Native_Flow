package com.stage.appToDo;

public record CategoryDto(Long id, String name) {

    public static CategoryDto fromEntity(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
