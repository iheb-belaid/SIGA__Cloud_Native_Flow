package com.stage.appToDo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepo;
    private final CategoryRepository categoryRepo;

    public TaskController(TaskRepository taskRepo, CategoryRepository categoryRepo) {
        this.taskRepo = taskRepo;
        this.categoryRepo = categoryRepo;
    }

    @GetMapping("/board")
    public TaskBoardDto board() {
        return new TaskBoardDto(
                findByStatus(Task.Status.TODO),
                findByStatus(Task.Status.IN_PROGRESS),
                findByStatus(Task.Status.DONE)
        );
    }

    @GetMapping("/{id}")
    public TaskDto findOne(@PathVariable Long id) {
        return TaskDto.fromEntity(getTask(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(@Valid @RequestBody TaskRequest request) {
        Task task = new Task();
        applyRequest(task, request);
        return TaskDto.fromEntity(taskRepo.save(task));
    }

    @PutMapping("/{id}")
    public TaskDto update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        Task task = getTask(id);
        applyRequest(task, request);
        return TaskDto.fromEntity(taskRepo.save(task));
    }

    @PatchMapping("/{id}/status")
    public TaskDto changeStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateRequest request) {
        Task task = getTask(id);
        task.setStatus(request.status());
        return TaskDto.fromEntity(taskRepo.save(task));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskRepo.delete(getTask(id));
    }

    private List<TaskDto> findByStatus(Task.Status status) {
        return taskRepo.findByStatus(status).stream()
                .sorted(Comparator
                        .comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Task::getId))
                .map(TaskDto::fromEntity)
                .toList();
    }

    private Task getTask(Long id) {
        return taskRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task inconnue " + id));
    }

    private void applyRequest(Task task, TaskRequest request) {
        Category category = categoryRepo.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categorie inconnue " + request.categoryId()));

        task.setTitle(request.title().trim());
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority());
        task.setStatus(request.status());
        task.setCategory(category);
    }
}
