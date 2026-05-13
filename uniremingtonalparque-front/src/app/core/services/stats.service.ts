import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ImpactStats {
  personasRegistradas: number;
  personasActivas: number;
  municipiosVisitados: number;
  personasAtendidas: number;
  totalAsistencias: number;
  totalEstudiantes: number;
  inversionSocialEstimada?: number;
}

export interface FacultadStats {
  facultad: string;
  totalAtenciones: number;
  beneficiariosUnicos: number;
  horasAcademicas: number;
}

export interface TerritorialStats {
  problematica: string;
  total: number;
  zonaMasAfectada: string;
}

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  private apiUrl = `${environment.apiUrl}/uni/analytics`;

  constructor(private http: HttpClient) {}

  getImpactStats(): Observable<ImpactStats> {
    return this.http.get<ImpactStats>(`${this.apiUrl}/impact`);
  }

  getFacultadStats(): Observable<FacultadStats[]> {
    return this.http.get<FacultadStats[]>(`${this.apiUrl}/facultades`);
  }

  getTerritorialStats(): Observable<TerritorialStats[]> {
    return this.http.get<TerritorialStats[]>(`${this.apiUrl}/territorial`);
  }

  getEfficiencyMetrics(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/efficiency`);
  }

  getRankingEstudiantes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ranking-estudiantes`);
  }

  getResumenEstrategico(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/resumen-estrategico`);
  }

  getCasosStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/casos`);
  }
}
