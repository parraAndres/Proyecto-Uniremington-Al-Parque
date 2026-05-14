import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, from, of, firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { db, UserRecord } from '../database/app-database';

export interface AuthResponse {
  token: string;
  documento: string;
  nombreCompleto: string;
  facultad: string;
  programa: string;
  tipo: string;
  rol: string;
  expiresIn: number;
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

  get currentUserValue(): any | null {
    return this.currentUserSubject.value;
  }

  private loadSession() {
    const userJson = localStorage.getItem('currentUser');
    if (userJson) {
      this.currentUserSubject.next(JSON.parse(userJson));
    }
  }

  login(documento: string, passwordHash: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/uni/auth/login`, { 
      documento, 
      password: passwordHash 
    }).pipe(
      tap(res => {
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('token', res.token);
          localStorage.setItem('currentUser', JSON.stringify(res));
          localStorage.setItem('activeUserDocumento', res.documento);
        }
        this.currentUserSubject.next(res);
      })
    );
  }

  async registerUser(user: any): Promise<void> {
    try {
      await firstValueFrom(this.http.post(`${this.apiUrl}/uni/auth/register`, user));
    } catch (error: any) {
      if (error.error && typeof error.error === 'object' && error.error.message) {
        throw new Error(error.error.message);
      } else if (error.error && typeof error.error === 'string') {
        throw new Error(error.error);
      } else {
        throw new Error('Error al conectarse con el servidor');
      }
    }
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
      localStorage.removeItem('currentUser');
      localStorage.removeItem('activeUserDocumento');
    }
    this.currentUserSubject.next(null);
  }

  forgotPassword(identificador: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/uni/auth/forgot-password?identificador=${identificador}`, {});
  }

  resetPassword(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/uni/auth/reset-password`, data);
  }
}
