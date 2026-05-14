import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Jornada {
  id?: number;
  nombre: string;
  fecha: string;
  municipio: string;
  vereda?: string;
  barrio?: string;
  descripcion?: string;
  imagenUrl?: string;
  estado: string;
  personalAsignado?: any[];
}

@Injectable({
  providedIn: 'root'
})
export class JornadaService {
  private apiUrl = `${environment.apiUrl}/jornadas`;

  constructor(private http: HttpClient) { }

  getJornadas(): Observable<Jornada[]> {
    return this.http.get<Jornada[]>(this.apiUrl);
  }

  createJornada(jornada: Jornada): Observable<Jornada> {
    return this.http.post<Jornada>(this.apiUrl, jornada);
  }

  updateJornada(id: number, jornada: Jornada): Observable<Jornada> {
    return this.http.put<Jornada>(`${this.apiUrl}/${id}`, jornada);
  }

  deleteJornada(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  asignarPersonal(jornadaId: number, usuarioId: string): Observable<Jornada> {
    return this.http.post<Jornada>(`${this.apiUrl}/${jornadaId}/asignar/${usuarioId}`, {});
  }

  quitarPersonal(jornadaId: number, usuarioId: string): Observable<Jornada> {
    return this.http.delete<Jornada>(`${this.apiUrl}/${jornadaId}/quitar/${usuarioId}`);
  }
}
