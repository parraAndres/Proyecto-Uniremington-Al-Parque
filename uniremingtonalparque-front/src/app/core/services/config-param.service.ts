import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ConfigParam {
  id?: number;
  tipo: string;
  valor: string;
  descripcion?: string;
  activo: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ConfigParamService {
  private apiUrl = `${environment.apiUrl}/uni/config`;

  constructor(private http: HttpClient) { }

  getParamsByTipo(tipo: string): Observable<ConfigParam[]> {
    return this.http.get<ConfigParam[]>(`${this.apiUrl}/${tipo}`);
  }

  saveParam(param: ConfigParam): Observable<ConfigParam> {
    if (param.id) {
      return this.http.put<ConfigParam>(`${this.apiUrl}/${param.id}`, param);
    }
    return this.http.post<ConfigParam>(this.apiUrl, param);
  }

  deleteParam(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
