import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { SyncService } from '../../parque/services/sync.service';
import { db } from '../../../core/database/app-database';
import { JornadaService } from '../../../core/services/jornada.service';
import { StatsService } from '../../../core/services/stats.service';

@Component({
  selector: 'app-docente-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './docente-dashboard.component.html'
})
export class DocenteDashboardComponent implements OnInit {
  user: any = null;
  isOnline = true;
  
  // Stats
  totalBeneficiarios = 0;
  totalAtenciones = 0;
  totalSeguimientos = 0;
  impactStats: any = {};
  
  jornadas: any[] = [];

  modulos = [
    {
      nombre: 'Supervisión de Servicios',
      desc: 'Revisar atenciones, validar registros y aprobar seguimientos',
      icon: 'fas fa-eye',
      ruta: '/docente/supervision',
      color: 'blue',
      subItems: ['Revisar atenciones', 'Validar registros', 'Aprobar seguimientos']
    },
    {
      nombre: 'Gestión de Casos',
      desc: 'Crear seguimientos, cambiar estados y programar visitas',
      icon: 'fas fa-folder-open',
      ruta: '/docente/gestion-casos',
      color: 'teal',
      subItems: ['Crear seguimiento', 'Cambiar estados', 'Programar visitas']
    },
    {
      nombre: 'Diagnóstico Territorial',
      desc: 'Registrar y priorizar problemáticas de la comunidad',
      icon: 'fas fa-map-marked-alt',
      ruta: '/diagnostico-territorial',
      color: 'green',
      subItems: ['Registrar problemáticas', 'Clasificar', 'Priorizar necesidades']
    },
    {
      nombre: 'Módulo Académico',
      desc: 'Participación estudiantil, control de horas y evaluación',
      icon: 'fas fa-graduation-cap',
      ruta: '/docente/modulo-academico',
      color: 'purple',
      subItems: ['Ver participación', 'Controlar horas', 'Evaluar participación']
    },
    {
      nombre: 'Reportes Académicos',
      desc: 'Estadísticas por jornada, horas acumuladas e impacto',
      icon: 'fas fa-chart-bar',
      ruta: '/docente/reportes-academicos',
      color: 'orange',
      subItems: ['Jornadas UAP', 'Horas acumuladas', 'Impacto por facultad']
    },
    {
      nombre: 'Panel de Control',
      desc: 'Administración general del programa UAP',
      icon: 'fas fa-cogs',
      ruta: '/panel-control',
      color: 'red',
      subItems: ['Gestión usuarios', 'Configuración', 'Estadísticas globales']
    }
  ];

  constructor(
    private authService: AuthService,
    private syncService: SyncService,
    private jornadaService: JornadaService,
    private statsService: StatsService,
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe((u: any) => this.user = u);
    this.syncService.networkStatus$.subscribe((s: boolean) => this.isOnline = s);
    this.loadStats();
    this.loadJornadas();
  }

  async loadStats() {
    try {
      this.totalBeneficiarios = (await db.beneficiaries.toArray()).length;
      this.totalAtenciones = (await db.servicios.toArray()).length;
      this.totalSeguimientos = (await db.seguimientos.toArray()).length;
    } catch (e) {}
    this.statsService.getImpactStats().subscribe({ next: (s: any) => this.impactStats = s, error: () => {} });
  }

  loadJornadas() {
    this.jornadaService.getJornadas().subscribe({
      next: (j: any[]) => this.jornadas = j.slice(0, 3),
      error: () => {}
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
