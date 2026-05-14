import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ServicioSocial {
  id?: number;
  beneficiario: {
    id: string;
    nombre: string;
    documento: string;
  };
  facultad: string;
  tipoServicio: string;
  resultadoAtencion: string;
  fechaServicio: string;
  estado: string;
  observaciones?: string;
  duracionMinutos?: number;
}

@Injectable({
  providedIn: 'root'
})
export class SocialService {
  private apiUrl = `${environment.apiUrl}/uni/social`;

  constructor(private http: HttpClient) {}

  saveServicio(data: any): Observable<ServicioSocial> {
    return this.http.post<ServicioSocial>(`${this.apiUrl}/servicios`, data);
  }

  getServiciosByEstudiante(estudianteId: string): Observable<ServicioSocial[]> {
    return this.http.get<ServicioSocial[]>(`${this.apiUrl}/servicios/estudiante/${estudianteId}`);
  }
}
