import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DocenteService {
  private apiUrl = `${environment.apiUrl}/uni/docente`;

  constructor(private http: HttpClient) { }

  getMisJornadas(id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/jornadas`);
  }

  getMisEstudiantes(id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/estudiantes`);
  }

  getCasosPendientes(id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/casos-pendientes`);
  }

  getStats(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/stats`);
  }
}
