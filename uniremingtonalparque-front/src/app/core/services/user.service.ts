import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface User {
  id: string;
  email: string;
  documento: string;
  nombreCompleto: string;
  facultad: string;
  programa: string;
  rol: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/usuarios`;

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  deleteUser(idOrEmail: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idOrEmail}`);
  }
}
