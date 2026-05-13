import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { db } from '../../../core/database/app-database';
import { StatsService } from '../../../core/services/stats.service';

@Component({
  selector: 'app-modulo-academico-docente',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './modulo-academico-docente.component.html'
})
export class ModuloAcademicoDocenteComponent implements OnInit {
  activeTab: 'participacion' | 'horas' | 'evaluacion' = 'participacion';

  participaciones: any[] = [];
  horasData: any[] = [];
  evaluaciones: any[] = [];
  rankingEstudiantes: any[] = [];

  totalHoras = 0;
  promHoras = 0;

  constructor(private statsService: StatsService) {}

  async ngOnInit() {
    await this.loadLocalData();
    this.loadRemoteRanking();
  }

  async loadLocalData() {
    try {
      const academico = await db.academico.toArray();
      const servicios = await db.servicios.toArray();

      // Participación estudiantil
      this.participaciones = academico.map(a => ({
        ...a,
        participacionPct: Math.min(100, (a.horas / 40) * 100) // 40h = 100%
      }));

      // Horas acumuladas
      const horasMap: { [key: string]: number } = {};
      academico.forEach(a => {
        horasMap[a.estudiante] = (horasMap[a.estudiante] || 0) + a.horas;
      });
      this.horasData = Object.entries(horasMap).map(([nombre, horas]) => ({ nombre, horas }))
        .sort((a, b) => b.horas - a.horas);
      this.totalHoras = this.horasData.reduce((sum, e) => sum + e.horas, 0);
      this.promHoras = this.horasData.length > 0 ? this.totalHoras / this.horasData.length : 0;

      // Evaluación: basado en servicios realizados por estudiante
      const evalMap: { [key: string]: { servicios: number; calidad: string } } = {};
      servicios.forEach(s => {
        const key = s.facultadResponsable;
        if (!evalMap[key]) evalMap[key] = { servicios: 0, calidad: '' };
        evalMap[key].servicios++;
      });
      this.evaluaciones = academico.map(a => {
        const nivel = a.horas >= 30 ? 'Excelente' : a.horas >= 15 ? 'Bueno' : 'Regular';
        return { ...a, nivel, color: nivel === 'Excelente' ? 'green' : nivel === 'Bueno' ? 'blue' : 'orange' };
      });
    } catch (e) {
      console.error('Error cargando datos académicos', e);
    }
  }

  loadRemoteRanking() {
    this.statsService.getRankingEstudiantes().subscribe({
      next: (res) => { if (res) this.rankingEstudiantes = res; },
      error: () => {}
    });
  }
}
