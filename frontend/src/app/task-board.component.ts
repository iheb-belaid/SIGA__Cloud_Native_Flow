import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TodoApiService } from './todo-api.service';
import { Status, Task, TaskBoard } from './todo.models';

@Component({
  selector: 'app-task-board',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './task-board.component.html',
  styleUrl: './task-board.component.css'
})
export class TaskBoardComponent implements OnInit {
  private readonly api = inject(TodoApiService);

  board: TaskBoard = { todo: [], doing: [], done: [] };
  draggedTask: Task | null = null;
  savingStatusForTaskId: number | null = null;

  ngOnInit(): void {
    this.loadBoard();
  }

  loadBoard(): void {
    this.api.getBoard().subscribe((board) => {
      this.board = board;
    });
  }

  drag(task: Task): void {
    this.draggedTask = task;
  }

  allowDrop(event: DragEvent): void {
    event.preventDefault();
  }

  drop(event: DragEvent, status: Status): void {
    event.preventDefault();
    if (!this.draggedTask || this.draggedTask.status === status) {
      this.draggedTask = null;
      return;
    }

    const task = this.draggedTask;
    const previousStatus = task.status;
    this.moveTaskLocally(task, status);
    this.savingStatusForTaskId = task.id;

    this.api.updateStatus(task.id, status).subscribe({
      next: () => {
        this.savingStatusForTaskId = null;
      },
      error: () => {
        this.moveTaskLocally(task, previousStatus);
        this.savingStatusForTaskId = null;
      }
    });

    this.draggedTask = null;
  }

  deleteTask(task: Task): void {
    const confirmed = window.confirm(`Supprimer la tache "${task.title}" ?`);
    if (!confirmed) {
      return;
    }

    this.api.deleteTask(task.id).subscribe(() => this.loadBoard());
  }

  priorityLabel(priority: Task['priority']): string {
    switch (priority) {
      case 'HIGH':
        return 'Haute';
      case 'MEDIUM':
        return 'Moyenne';
      default:
        return 'Faible';
    }
  }

  private moveTaskLocally(task: Task, nextStatus: Status): void {
    this.removeTask(task.id);
    task.status = nextStatus;
    this.tasksFor(nextStatus).push(task);
  }

  private removeTask(taskId: number): void {
    this.board.todo = this.board.todo.filter((task) => task.id !== taskId);
    this.board.doing = this.board.doing.filter((task) => task.id !== taskId);
    this.board.done = this.board.done.filter((task) => task.id !== taskId);
  }

  private tasksFor(status: Status): Task[] {
    switch (status) {
      case 'TODO':
        return this.board.todo;
      case 'IN_PROGRESS':
        return this.board.doing;
      case 'DONE':
        return this.board.done;
    }
  }
}
