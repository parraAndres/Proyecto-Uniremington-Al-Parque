import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { SyncService } from '../../parque/services/sync.service';
import { db } from '../../../core/database/app-database';
import { JornadaService } from '../../../core/services/jornada.service';

@Component({
  selector: 'app-estudiante-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './estudiante-dashboard.component.html'
})
export class EstudianteDashboardComponent implements OnInit {
  user: any = null;
  isOnline = true;
  pendingCount = 0;
  jornadaActual: any = null;
  totalAtenciones = 0;
  totalBeneficiarios = 0;
  totalSeguimientos = 0;
  
  currentDate = new Date();

  modulos = [
    {
      nombre: 'Registrar Beneficiario',
      descripcion: 'Registra nuevas personas al programa',
      icon: 'fas fa-user-plus',
      ruta: '/parque/beneficiarios',
      color: 'blue',
      badge: null
    },
    {
      nombre: 'Registro de Atención',
      descripcion: 'Documenta servicios prestados',
      icon: 'fas fa-hands-helping',
      ruta: '/estudiante/atencion',
      color: 'green',
      badge: null
    },
    {
      nombre: 'Historial de Atenciones',
      descripcion: 'Revisa los servicios realizados',
      icon: 'fas fa-history',
      ruta: '/estudiante/historial',
      color: 'purple',
      badge: null
    },
    {
      nombre: 'Seguimiento',
      descripcion: 'Actualiza avances de casos',
      icon: 'fas fa-tasks',
      ruta: '/seguimiento',
      color: 'orange',
      badge: null
    },
    {
      nombre: 'Sincronización Offline',
      descripcion: 'Gestiona datos pendientes',
      icon: 'fas fa-sync-alt',
      ruta: '/offline-status',
      color: 'cyan',
      badge: 'pendingCount'
    }
  ];

  constructor(
    private authService: AuthService,
    private syncService: SyncService,
    private jornadaService: JornadaService
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe((u: any) => this.user = u);
    this.syncService.networkStatus$.subscribe((s: boolean) => this.isOnline = s);
    this.syncService.pendingCount$.subscribe((n: number) => {
      this.pendingCount = n;
      this.modulos.find(m => m.badge === 'pendingCount')!.badge = n > 0 ? n.toString() : null;
    });
    this.loadStats();
    this.loadJornadaActual();
  }

  async loadStats() {
    try {
      this.totalAtenciones = (await db.servicios.toArray()).length;
      this.totalBeneficiarios = (await db.beneficiaries.toArray()).length;
      this.totalSeguimientos = (await db.seguimientos.toArray()).length;
    } catch (e) {}
  }

  loadJornadaActual() {
    this.jornadaService.getJornadas().subscribe({
      next: (jornadas: any[]) => {
        this.jornadaActual = jornadas.find((j: any) => j.estado === 'EN_CURSO') || jornadas.find((j: any) => j.estado === 'PROGRAMADA') || null;
      },
      error: () => {}
    });
  }

  get horaActual(): string {
    return this.currentDate.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
  }
  
  get fechaActual(): string {
    return this.currentDate.toLocaleDateString('es-CO', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
  }
}
