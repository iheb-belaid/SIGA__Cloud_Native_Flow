package com.stage.appToDo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryIntegrationTests {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByStatusReturnsPersistedTasks() {
        Category category = new Category();
        category.setName("Observabilite");
        category = categoryRepository.save(category);

        Task task = new Task();
        task.setTitle("Ajouter Prometheus");
        task.setDueDate(LocalDate.now().plusDays(2));
        task.setPriority(Task.Priority.HIGH);
        task.setStatus(Task.Status.IN_PROGRESS);
        task.setCategory(category);
        taskRepository.save(task);

        List<Task> tasks = taskRepository.findByStatus(Task.Status.IN_PROGRESS);

        assertThat(tasks).hasSize(1);
        assertThat(tasks.getFirst().getTitle()).isEqualTo("Ajouter Prometheus");
        assertThat(tasks.getFirst().getCategory().getName()).isEqualTo("Observabilite");
    }
}
