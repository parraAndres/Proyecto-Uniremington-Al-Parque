import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, finalize, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export abstract class BaseService<T> {
  protected apiUrl = environment.apiUrl;
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  constructor(protected http: HttpClient, protected resourcePath: string) {}

  getAll(): Observable<T[]> {
    this.loadingSubject.next(true);
    return this.http.get<T[]>(`${this.apiUrl}/${this.resourcePath}`).pipe(
      finalize(() => this.loadingSubject.next(false))
    );
  }

  getById(id: any): Observable<T> {
    this.loadingSubject.next(true);
    return this.http.get<T>(`${this.apiUrl}/${this.resourcePath}/${id}`).pipe(
      finalize(() => this.loadingSubject.next(false))
    );
  }

  create(item: T): Observable<T> {
    this.loadingSubject.next(true);
    return this.http.post<T>(`${this.apiUrl}/${this.resourcePath}`, item).pipe(
      finalize(() => this.loadingSubject.next(false))
    );
  }

  update(id: any, item: T): Observable<T> {
    this.loadingSubject.next(true);
    return this.http.put<T>(`${this.apiUrl}/${this.resourcePath}/${id}`, item).pipe(
      finalize(() => this.loadingSubject.next(false))
    );
  }

  delete(id: any): Observable<void> {
    this.loadingSubject.next(true);
    return this.http.delete<void>(`${this.apiUrl}/${this.resourcePath}/${id}`).pipe(
      finalize(() => this.loadingSubject.next(false))
    );
  }
}
