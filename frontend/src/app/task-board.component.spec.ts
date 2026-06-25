import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { TaskBoardComponent } from './task-board.component';
import { TodoApiService } from './todo-api.service';
import { TaskBoard } from './todo.models';

describe('TaskBoardComponent', () => {
  const board: TaskBoard = {
    todo: [
      {
        id: 1,
        title: 'Configurer GHCR',
        dueDate: '2026-07-10',
        priority: 'HIGH',
        status: 'TODO',
        category: { id: 1, name: 'DevOps' }
      }
    ],
    doing: [],
    done: []
  };

  let api: {
    getBoard: ReturnType<typeof vi.fn>;
    deleteTask: ReturnType<typeof vi.fn>;
    updateStatus: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    api = {
      getBoard: vi.fn(() => of(board)),
      deleteTask: vi.fn(() => of(void 0)),
      updateStatus: vi.fn(() => of(board.todo[0]))
    };

    await TestBed.configureTestingModule({
      imports: [TaskBoardComponent],
      providers: [
        provideRouter([]),
        { provide: TodoApiService, useValue: api }
      ]
    }).compileComponents();
  });

  it('should render tasks loaded from the API', () => {
    const fixture = TestBed.createComponent(TaskBoardComponent);
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    expect(api.getBoard).toHaveBeenCalledTimes(1);
    expect(element.textContent).toContain('Configurer GHCR');
    expect(element.textContent).toContain('DevOps');
    expect(element.textContent).toContain('Haute');
  });

  it('should delete a task and reload the board after confirmation', () => {
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true);
    const fixture = TestBed.createComponent(TaskBoardComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    component.deleteTask(board.todo[0]);

    expect(api.deleteTask).toHaveBeenCalledWith(1);
    expect(api.getBoard).toHaveBeenCalledTimes(2);

    confirmSpy.mockRestore();
  });
});
