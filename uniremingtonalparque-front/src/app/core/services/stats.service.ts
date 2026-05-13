import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ImpactStats {
  municipiosVisitados: number;
  personasAtendidas: number;
  personasActivas: number;
  personasRegistradas: number;
  totalAsistencias: number;
  totalEstudiantes: number;
}

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getImpactStats(): Observable<ImpactStats> {
    return this.http.get<ImpactStats>(`${this.apiUrl}/analytics/impact`);
  }
}
