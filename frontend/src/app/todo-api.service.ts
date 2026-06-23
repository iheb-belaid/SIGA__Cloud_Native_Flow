import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Category, Task, TaskBoard, TaskPayload, Status } from './todo.models';

@Injectable({ providedIn: 'root' })
export class TodoApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8081/api';

  getBoard() {
    return this.http.get<TaskBoard>(`${this.apiUrl}/tasks/board`);
  }

  getTask(id: number) {
    return this.http.get<Task>(`${this.apiUrl}/tasks/${id}`);
  }

  createTask(payload: TaskPayload) {
    return this.http.post<Task>(`${this.apiUrl}/tasks`, payload);
  }

  updateTask(id: number, payload: TaskPayload) {
    return this.http.put<Task>(`${this.apiUrl}/tasks/${id}`, payload);
  }

  deleteTask(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/tasks/${id}`);
  }

  updateStatus(id: number, status: Status) {
    return this.http.patch<Task>(`${this.apiUrl}/tasks/${id}/status`, { status });
  }

  getCategories() {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  createCategory(name: string) {
    return this.http.post<Category>(`${this.apiUrl}/categories`, { name });
  }
}
