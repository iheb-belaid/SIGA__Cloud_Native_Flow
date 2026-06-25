package com.stage.appToDo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        categoryRepository.deleteAll();

        category = new Category();
        category.setName("DevOps");
        category = categoryRepository.save(category);
    }

    @Test
    void createTaskReturnsCreatedTask() throws Exception {
        Map<String, Object> payload = Map.of(
                "title", "Configurer la pipeline",
                "dueDate", LocalDate.now().plusDays(3).toString(),
                "priority", "HIGH",
                "status", "TODO",
                "categoryId", category.getId()
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Configurer la pipeline"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.category.id").value(category.getId()))
                .andExpect(jsonPath("$.category.name").value("DevOps"));
    }

    @Test
    void createTaskWithPastDueDateReturnsValidationError() throws Exception {
        Map<String, Object> payload = Map.of(
                "title", "Date invalide",
                "dueDate", LocalDate.now().minusDays(1).toString(),
                "priority", "MEDIUM",
                "status", "TODO",
                "categoryId", category.getId()
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.dueDate").value("La date limite doit etre aujourd'hui ou dans le futur"));
    }

    @Test
    void boardReturnsTasksGroupedByStatus() throws Exception {
        saveTask("Configurer SonarQube", Task.Status.TODO, LocalDate.now().plusDays(4));
        saveTask("Ecrire le Dockerfile", Task.Status.IN_PROGRESS, LocalDate.now().plusDays(2));
        saveTask("Presenter la demo", Task.Status.DONE, LocalDate.now().plusDays(7));

        mockMvc.perform(get("/api/tasks/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todo.length()").value(1))
                .andExpect(jsonPath("$.doing.length()").value(1))
                .andExpect(jsonPath("$.done.length()").value(1))
                .andExpect(jsonPath("$.todo[0].title").value("Configurer SonarQube"))
                .andExpect(jsonPath("$.doing[0].title").value("Ecrire le Dockerfile"))
                .andExpect(jsonPath("$.done[0].title").value("Presenter la demo"));
    }

    private void saveTask(String title, Task.Status status, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDueDate(dueDate);
        task.setPriority(Task.Priority.MEDIUM);
        task.setStatus(status);
        task.setCategory(category);
        taskRepository.save(task);
    }
}
