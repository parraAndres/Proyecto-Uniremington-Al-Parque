import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { db } from '../../../core/database/app-database';
import { StatsService } from '../../../core/services/stats.service';

@Component({
  selector: 'app-reportes-academicos',
  standalone: true,
  imports: [CommonModule, RouterModule, DecimalPipe],
  templateUrl: './reportes-academicos.component.html'
})
export class ReportesAcademicosComponent implements OnInit {
  activeTab: 'jornadas' | 'horas' | 'impacto' = 'jornadas';

  // Data
  jornadasStats: any[] = [];
  horasEstudiantes: any[] = [];
  impactoFacultad: any[] = [];
  totalHorasAcumuladas = 0;
  totalEstudiantes = 0;

  constructor(private statsService: StatsService) {}

  async ngOnInit() {
    await this.loadLocalStats();
    this.loadRemoteStats();
  }

  async loadLocalStats() {
    try {
      const academico = await db.academico.toArray();
      const servicios = await db.servicios.toArray();
      const beneficiarios = await db.beneficiaries.toArray();

      // Horas por estudiante
      const horasMap: { [key: string]: { nombre: string; programa: string; horas: number } } = {};
      for (const a of academico) {
        if (!horasMap[a.estudiante]) {
          horasMap[a.estudiante] = { nombre: a.estudiante, programa: a.programa, horas: 0 };
        }
        horasMap[a.estudiante].horas += a.horas;
      }
      this.horasEstudiantes = Object.values(horasMap).sort((a, b) => b.horas - a.horas);
      this.totalHorasAcumuladas = this.horasEstudiantes.reduce((sum, e) => sum + e.horas, 0);
      this.totalEstudiantes = this.horasEstudiantes.length;

      // Impacto por facultad (desde servicios)
      const facultadMap: { [key: string]: { facultad: string; servicios: number; beneficiarios: Set<string> } } = {};
      for (const s of servicios) {
        const fac = s.facultadResponsable || 'Sin Facultad';
        if (!facultadMap[fac]) {
          facultadMap[fac] = { facultad: fac, servicios: 0, beneficiarios: new Set() };
        }
        facultadMap[fac].servicios++;
        if (s.beneficiarioId) facultadMap[fac].beneficiarios.add(s.beneficiarioId);
      }
      this.impactoFacultad = Object.values(facultadMap).map(f => ({
        ...f,
        beneficiariosUnicos: f.beneficiarios.size
      }));

      // Jornadas stats mock (from local data)
      this.jornadasStats = [
        { jornada: 'Jornada Marinilla', fecha: '2026-04-15', estudiantes: academico.length, servicios: servicios.filter(s => s.beneficiarioId).length },
        { jornada: 'Jornada El Santuario', fecha: '2026-05-02', estudiantes: Math.floor(academico.length * 0.7), servicios: Math.floor(servicios.length * 0.5) }
      ];
    } catch (e) {
      console.error('Error cargando estadísticas locales', e);
    }
  }

  loadRemoteStats() {
    this.statsService.getFacultadStats().subscribe({
      next: (res) => {
        if (res && res.length > 0) {
          this.impactoFacultad = res.map((f: any) => ({
            facultad: f.facultad,
            servicios: f.totalAtenciones,
            beneficiariosUnicos: f.beneficiariosUnicos,
            horas: f.horasAcademicas
          }));
        }
      },
      error: () => {} // Use local data if offline
    });
  }
}
