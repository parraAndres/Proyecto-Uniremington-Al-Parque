import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { db, UserRecord } from '../database/app-database';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<UserRecord | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    if (isPlatformBrowser(this.platformId)) {
      this.checkAndCreateAdmin().then(() => {
        this.loadSession();
      });
    }
  }

  get isAuthenticated(): boolean {
    return !!this.currentUserSubject.value;
  }

  private async checkAndCreateAdmin() {
    const adminEmail = '1120957560';
    const admin = await db.users.get(adminEmail);
    if (!admin) {
      await db.users.put({
        email: adminEmail,
        nombreCompleto: 'Administrador del Sistema',
        passwordHash: btoa('1120957560'),
        role: 'admin'
      });
    }
  }

  private loadSession() {
    const sessionEmail = localStorage.getItem('activeUserEmail');
    if (sessionEmail) {
      db.users.get(sessionEmail).then(user => {
        if (user) {
          this.currentUserSubject.next(user);
        }
      });
    }
  }

  async registerUser(user: UserRecord): Promise<void> {
    const existing = await db.users.get(user.email);
    if (existing) {
      throw new Error('El usuario ya está registrado con este correo');
    }
    // Simulate simple password hash for offline mode
    user.passwordHash = btoa(user.passwordHash); 
    await db.users.put(user);
  }

  async login(email: string, passwordHash: string): Promise<UserRecord> {
    const user = await db.users.get(email);
    if (!user) {
      throw new Error('Usuario no encontrado');
    }
    
    // Check simulated hash
    if (user.passwordHash !== btoa(passwordHash)) {
      throw new Error('Credenciales incorrectas');
    }

    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('activeUserEmail', user.email);
    }
    this.currentUserSubject.next(user);
    return user;
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('activeUserEmail');
    }
    this.currentUserSubject.next(null);
  }
}
