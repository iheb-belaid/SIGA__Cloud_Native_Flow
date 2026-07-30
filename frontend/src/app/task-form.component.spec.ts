import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { TaskFormComponent } from './task-form.component';
import { TodoApiService } from './todo-api.service';

describe('TaskFormComponent', () => {
  let api: {
    getCategories: ReturnType<typeof vi.fn>;
    createTask: ReturnType<typeof vi.fn>;
    createCategory: ReturnType<typeof vi.fn>;
    getTask: ReturnType<typeof vi.fn>;
    updateTask: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    api = {
      getCategories: vi.fn(() => of([{ id: 3, name: 'Retest' }])),
      createTask: vi.fn(() => of({
        id: 1,
        title: 'Task browser test',
        dueDate: '2026-08-10',
        priority: 'MEDIUM',
        status: 'IN_PROGRESS',
        category: { id: 3, name: 'Retest' }
      })),
      createCategory: vi.fn(),
      getTask: vi.fn(),
      updateTask: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [TaskFormComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => null } } }
        },
        { provide: TodoApiService, useValue: api }
      ]
    }).compileComponents();
  });

  it('should convert the selected category id to a number before saving', () => {
    const fixture = TestBed.createComponent(TaskFormComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    component.form.setValue({
      title: 'Task browser test',
      dueDate: '2026-08-10',
      priority: 'MEDIUM',
      status: 'IN_PROGRESS',
      categoryId: '3'
    });

    component.save();

    expect(api.createTask).toHaveBeenCalledWith({
      title: 'Task browser test',
      dueDate: '2026-08-10',
      priority: 'MEDIUM',
      status: 'IN_PROGRESS',
      categoryId: 3
    });
    expect(navigateSpy).toHaveBeenCalledWith('/tasks');
  });
});
