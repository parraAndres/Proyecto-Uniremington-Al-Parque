import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface NotificacionUniremington {
  id: number;
  titulo: string;
  mensaje: string;
  leida: boolean;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class UniNotificacionService {
  private apiUrl = `${environment.apiUrl}/uni/notificaciones`;

  constructor(private http: HttpClient) { }

  getNotificaciones(): Observable<NotificacionUniremington[]> {
    return this.http.get<NotificacionUniremington[]>(this.apiUrl);
  }

  getUnreadCount(): Observable<{ unreadCount: number }> {
    return this.http.get<{ unreadCount: number }>(`${this.apiUrl}/unread-count`);
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/read`, {});
  }
}
