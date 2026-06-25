import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TodoApiService } from './todo-api.service';
import { TaskPayload } from './todo.models';

describe('TodoApiService', () => {
  let service: TodoApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(TodoApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should request the task board from the backend API', () => {
    service.getBoard().subscribe();

    const request = httpMock.expectOne('http://localhost:8081/api/tasks/board');
    expect(request.request.method).toBe('GET');
    request.flush({ todo: [], doing: [], done: [] });
  });

  it('should post a new task payload to the backend API', () => {
    const payload: TaskPayload = {
      title: 'Documenter la demo',
      dueDate: '2026-07-10',
      priority: 'HIGH',
      status: 'TODO',
      categoryId: 4
    };

    service.createTask(payload).subscribe();

    const request = httpMock.expectOne('http://localhost:8081/api/tasks');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);
    request.flush({
      id: 1,
      ...payload,
      category: { id: 4, name: 'Stage' }
    });
  });
});
