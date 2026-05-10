import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, from, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { db, UserRecord } from '../database/app-database';

export interface AuthResponse {
  token: string;
  tipo: string;
  email: string;
  nombre: string;
  rol: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = environment.apiUrl;
  private currentUserSubject = new BehaviorSubject<any | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    if (isPlatformBrowser(this.platformId)) {
      this.loadSession();
    }
  }

  get isAuthenticated(): boolean {
    return !!this.currentUserSubject.value;
  }

  private loadSession() {
    const userJson = localStorage.getItem('currentUser');
    if (userJson) {
      this.currentUserSubject.next(JSON.parse(userJson));
    }
  }

  login(email: string, passwordHash: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, { 
      email, 
      password: passwordHash 
    }).pipe(
      tap(res => {
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('token', res.token);
          localStorage.setItem('currentUser', JSON.stringify(res));
          localStorage.setItem('activeUserEmail', res.email);
        }
        this.currentUserSubject.next(res);
      })
    );
  }

  async registerUser(user: UserRecord): Promise<void> {
    // Para registro, se mantiene la lógica offline pero se recomienda 
    // crear un endpoint POST /usuarios/register en el backend.
    const existing = await db.users.get(user.email);
    if (existing) {
      throw new Error('El usuario ya está registrado');
    }
    await db.users.put(user);
    // Opcional: Llamada al backend
    // return this.http.post(`${this.apiUrl}/usuarios/register`, user).toPromise();
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
      localStorage.removeItem('currentUser');
      localStorage.removeItem('activeUserEmail');
    }
    this.currentUserSubject.next(null);
  }
}
