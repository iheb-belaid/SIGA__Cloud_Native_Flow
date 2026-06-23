package com.stage.appToDo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
public class Task {

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Status   { TODO, IN_PROGRESS, DONE }

    /*----------  Identifiant  ----------*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    

    /*----------  Titre  ----------*/
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100,
            message = "Le titre doit contenir entre {min} et {max} caractères")
    private String title;

    /*----------  Date d'échéance  ----------*/
    @FutureOrPresent(message = "La date limite doit être aujourd’hui ou dans le futur")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    /*----------  Priorité  ----------*/
    @NotNull(message = "La priorité est obligatoire")
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    /*----------  Statut  ----------*/
    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    private Status status = Status.TODO;

    /*----------  Catégorie  ----------*/
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull(message = "La catégorie est obligatoire")
    private Category category;

    /*==========  Constructeurs  ==========*/
    public Task() { }
    public Task(String title) { this.title = title; }

    /*==========  Getters / Setters  ==========*/
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
