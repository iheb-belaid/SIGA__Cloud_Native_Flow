import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TodoApiService } from './todo-api.service';
import { Category, Priority, Status, TaskPayload } from './todo.models';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.css'
})
export class TaskFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(TodoApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly priorities: Priority[] = ['HIGH', 'MEDIUM', 'LOW'];
  readonly statuses: { value: Status; label: string }[] = [
    { value: 'TODO', label: 'A faire' },
    { value: 'IN_PROGRESS', label: 'En cours' },
    { value: 'DONE', label: 'Terminee' }
  ];

  categories: Category[] = [];
  taskId: number | null = null;
  showCategoryModal = false;
  saving = false;
  categorySaving = false;
  categoryError = '';
  formError = '';
  loadingCategories = true;

  readonly form = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    dueDate: [''],
    priority: ['MEDIUM' as Priority, Validators.required],
    status: ['TODO' as Status, Validators.required],
    categoryId: [0, [Validators.required, Validators.min(1)]]
  });

  readonly categoryForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(100)]]
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.taskId = idParam ? Number(idParam) : null;
    this.loadCategories();

    if (this.taskId) {
      this.api.getTask(this.taskId).subscribe((task) => {
        this.form.patchValue({
          title: task.title,
          dueDate: task.dueDate ?? '',
          priority: task.priority,
          status: task.status,
          categoryId: task.category.id
        });
      });
    }
  }

  save(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      return;
    }

    this.saving = true;
    this.formError = '';
    const payload: TaskPayload = {
      title: this.form.controls.title.value.trim(),
      dueDate: this.form.controls.dueDate.value || null,
      priority: this.form.controls.priority.value,
      status: this.form.controls.status.value,
      categoryId: this.form.controls.categoryId.value
    };

    const request = this.taskId
      ? this.api.updateTask(this.taskId, payload)
      : this.api.createTask(payload);

    request.subscribe({
      next: () => this.router.navigateByUrl('/tasks'),
      error: (error: HttpErrorResponse) => {
        this.saving = false;
        this.formError = this.resolveErrorMessage(error, 'Impossible d enregistrer la tache pour le moment.');
      }
    });
  }

  openCategoryModal(): void {
    this.showCategoryModal = true;
    this.categoryError = '';
  }

  closeCategoryModal(): void {
    this.showCategoryModal = false;
    this.categoryForm.reset({ name: '' });
    this.categoryError = '';
  }

  addCategory(): void {
    this.categoryForm.markAllAsTouched();
    if (this.categoryForm.invalid) {
      return;
    }

    this.categorySaving = true;
    this.categoryError = '';
    const name = this.categoryForm.controls.name.value.trim();
    this.api.createCategory(name).subscribe({
      next: (category) => {
        this.categories = [...this.categories, category].sort((left, right) => left.name.localeCompare(right.name));
        this.form.controls.categoryId.setValue(category.id);
        this.form.controls.categoryId.markAsTouched();
        this.categorySaving = false;
        this.closeCategoryModal();
      },
      error: (error: HttpErrorResponse) => {
        this.categorySaving = false;
        this.categoryError = this.resolveErrorMessage(error, 'Impossible de creer la categorie pour le moment.');
      }
    });
  }

  titleText(): string {
    return this.taskId ? 'Modifier la tache' : 'Nouvelle tache';
  }

  private loadCategories(): void {
    this.api.getCategories().subscribe((categories) => {
      this.categories = categories;
      this.loadingCategories = false;
    }, () => {
      this.loadingCategories = false;
      this.formError = 'Impossible de charger les categories. Verifiez que le backend est demarre.';
    });
  }

  private resolveErrorMessage(error: HttpErrorResponse, fallback: string): string {
    if (error.status === 0) {
      return 'Le backend ne repond pas encore. Verifiez que le service Spring Boot est demarre.';
    }

    const errors = error.error?.errors;
    if (errors && typeof errors === 'object') {
      const firstMessage = Object.values(errors)[0];
      if (typeof firstMessage === 'string') {
        return firstMessage;
      }
    }

    if (typeof error.error?.detail === 'string' && error.error.detail.trim() !== '') {
      return error.error.detail;
    }

    return fallback;
  }
}
