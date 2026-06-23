export type Priority = 'HIGH' | 'MEDIUM' | 'LOW';
export type Status = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface Category {
  id: number;
  name: string;
}

export interface Task {
  id: number;
  title: string;
  dueDate: string | null;
  priority: Priority;
  status: Status;
  category: Category;
}

export interface TaskBoard {
  todo: Task[];
  doing: Task[];
  done: Task[];
}

export interface TaskPayload {
  title: string;
  dueDate: string | null;
  priority: Priority;
  status: Status;
  categoryId: number;
}
