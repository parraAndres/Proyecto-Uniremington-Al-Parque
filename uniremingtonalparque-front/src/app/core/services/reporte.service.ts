import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = `${environment.apiUrl}/uni/reportes`;

  constructor(private http: HttpClient) { }

  downloadExcel(filters: any) {
    let params = new HttpParams();
    if (filters.inicio) params = params.set('inicio', filters.inicio);
    if (filters.fin) params = params.set('fin', filters.fin);
    if (filters.municipio) params = params.set('municipio', filters.municipio);
    if (filters.vereda) params = params.set('vereda', filters.vereda);
    if (filters.barrio) params = params.set('barrio', filters.barrio);

    this.http.get(`${this.apiUrl}/excel`, { params, responseType: 'blob' }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'reporte_atenciones.xlsx';
      a.click();
    });
  }

  downloadPdf(filters: any) {
    let params = new HttpParams();
    if (filters.inicio) params = params.set('inicio', filters.inicio);
    if (filters.fin) params = params.set('fin', filters.fin);
    if (filters.municipio) params = params.set('municipio', filters.municipio);
    if (filters.vereda) params = params.set('vereda', filters.vereda);
    if (filters.barrio) params = params.set('barrio', filters.barrio);

    this.http.get(`${this.apiUrl}/pdf`, { params, responseType: 'blob' }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'reporte_atenciones.pdf';
      a.click();
    });
  }
}
