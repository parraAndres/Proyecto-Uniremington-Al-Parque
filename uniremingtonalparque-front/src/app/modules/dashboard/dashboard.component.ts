import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  userRole: string = '';
  userName: string = '';

  // Docente/Coordinador modules
  docenteModulos = [
    { nombre: 'Supervisión de Servicios', icon: 'fas fa-eye', ruta: '/docente/supervision', desc: 'Revisar atenciones, validar y aprobar seguimientos', color: 'blue' },
    { nombre: 'Gestión de Casos', icon: 'fas fa-folder-open', ruta: '/docente/gestion-casos', desc: 'Crear seguimientos, cambiar estados, programar visitas', color: 'teal' },
    { nombre: 'Diagnóstico Territorial', icon: 'fas fa-map-marked-alt', ruta: '/diagnostico-territorial', desc: 'Registrar, clasificar y priorizar problemáticas', color: 'green' },
    { nombre: 'Módulo Académico', icon: 'fas fa-graduation-cap', ruta: '/docente/modulo-academico', desc: 'Participación estudiantil, control de horas y evaluación', color: 'purple' },
    { nombre: 'Reportes Académicos', icon: 'fas fa-chart-bar', ruta: '/docente/reportes-academicos', desc: 'Estadísticas por jornada, horas e impacto por facultad', color: 'orange' },
    { nombre: 'Panel de Control', icon: 'fas fa-cogs', ruta: '/panel-control', desc: 'Administración general del programa', color: 'red' }
  ];

  // Estudiante_Facultad modules
  estudianteModulos = [
    { nombre: 'Registrar Beneficiario', icon: 'fas fa-user-plus', ruta: '/parque/beneficiarios', desc: 'Registrar, buscar y editar beneficiarios', color: 'blue' },
    { nombre: 'Registro de Atención', icon: 'fas fa-hands-helping', ruta: '/estudiante/atencion', desc: 'Seleccionar beneficiario, servicio y registrar tiempo', color: 'green' },
    { nombre: 'Historial de Atenciones', icon: 'fas fa-history', ruta: '/estudiante/historial', desc: 'Servicios realizados y estado de casos', color: 'purple' },
    { nombre: 'Seguimiento', icon: 'fas fa-tasks', ruta: '/seguimiento', desc: 'Agregar avances y actualizar estado de casos', color: 'orange' },
    { nombre: 'Sincronización Offline', icon: 'fas fa-sync-alt', ruta: '/offline-status', desc: 'Gestión de datos pendientes para sincronizar', color: 'cyan' }
  ];

  // Beneficiario (cliente) modules
  clienteModulos = [
    { nombre: 'Mis Atenciones Recibidas', icon: 'fas fa-hand-holding-heart', ruta: '/beneficiario/mis-atenciones', desc: 'Servicios recibidos, fechas y facultad responsable', color: 'blue' },
    { nombre: 'Seguimiento de Caso', icon: 'fas fa-search-plus', ruta: '/beneficiario/seguimiento-caso', desc: 'Avances, recomendaciones y próximas citas', color: 'teal' },
    { nombre: 'Mi Perfil', icon: 'fas fa-user-edit', ruta: '/beneficiario/perfil', desc: 'Actualizar teléfono, dirección y datos básicos', color: 'purple' }
  ];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        const rawRole = (user.role || user.rol || user.tipo || 'cliente').toString().toLowerCase();
        this.userName = user.nombreCompleto;

        // Role mapping
        if (rawRole === 'profesor' || rawRole === 'docente' || rawRole === 'coordinador') {
          this.userRole = 'docente';
          this.router.navigate(['/docente']);
          return;
        } else if (rawRole === 'estudiante' || rawRole === 'estudiante_facultad') {
          this.userRole = 'estudiante';
          this.router.navigate(['/estudiante']);
          return;
        } else if (rawRole === 'cliente' || rawRole === 'beneficiario') {
          this.userRole = 'cliente';
          this.router.navigate(['/beneficiario']);
          return;
        } else if (rawRole === 'admin') {
          this.userRole = 'admin';
          this.router.navigate(['/panel-control']);
          return;
        }

        this.userRole = rawRole;
      }
    });
  }
}
