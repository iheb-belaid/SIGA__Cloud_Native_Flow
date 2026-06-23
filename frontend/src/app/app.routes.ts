import { Routes } from '@angular/router';
import { TaskBoardComponent } from './task-board.component';
import { TaskFormComponent } from './task-form.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'tasks' },
  { path: 'tasks', component: TaskBoardComponent },
  { path: 'tasks/new', component: TaskFormComponent },
  { path: 'tasks/edit/:id', component: TaskFormComponent },
  { path: '**', redirectTo: 'tasks' }
];
