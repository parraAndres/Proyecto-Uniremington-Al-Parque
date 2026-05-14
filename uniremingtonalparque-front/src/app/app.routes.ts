import { Routes } from '@angular/router';
import { LandingComponent } from './modules/landing/landing.component';
import { LoginComponent } from './modules/auth/login/login.component';
import { RegisterComponent } from './modules/auth/register/register.component';
import { ResetPasswordComponent } from './modules/auth/reset-password/reset-password.component';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { authGuard } from './core/guards/auth.guard';
import { BeneficiaryFormComponent } from './modules/parque/components/beneficiary-form/beneficiary-form.component';
import { ServiciosFormComponent } from './modules/parque/components/servicios-form/servicios-form.component';
import { IndicadoresComponent } from './modules/indicadores/indicadores.component';
import { SeguimientoComponent } from './modules/seguimiento/seguimiento.component';
import { DiagnosticoComponent } from './modules/diagnostico/diagnostico.component';
import { AcademicoComponent } from './modules/academico/academico.component';
import { RecursosComponent } from './modules/recursos/recursos.component';
import { OfflineStatusComponent } from './modules/offline/offline-status.component';
import { PanelControlComponent } from './modules/panel/panel-control.component';
// Docente Dashboard + Modules
import { DocenteDashboardComponent } from './modules/docente/docente-dashboard/docente-dashboard.component';
import { SupervisionComponent } from './modules/docente/supervision/supervision.component';
import { GestionCasosComponent } from './modules/docente/gestion-casos/gestion-casos.component';
import { ModuloAcademicoDocenteComponent } from './modules/docente/modulo-academico-docente/modulo-academico-docente.component';
import { ReportesAcademicosComponent } from './modules/docente/reportes-academicos/reportes-academicos.component';
// Estudiante Modules
import { EstudianteDashboardComponent } from './modules/estudiante/estudiante-dashboard/estudiante-dashboard.component';
import { AtencionComponent } from './modules/estudiante/atencion/atencion.component';
import { HistorialComponent } from './modules/estudiante/historial/historial.component';
// Beneficiario Modules
import { BeneficiarioDashboardComponent } from './modules/beneficiario/beneficiario-dashboard/beneficiario-dashboard.component';
import { MisAtencionesComponent } from './modules/beneficiario/mis-atenciones/mis-atenciones.component';
import { SeguimientoCasoBeneficiarioComponent } from './modules/beneficiario/seguimiento-caso-beneficiario/seguimiento-caso-beneficiario.component';
import { PerfilBeneficiarioComponent } from './modules/beneficiario/perfil-beneficiario/perfil-beneficiario.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'noticia/:id', loadComponent: () => import('./modules/landing/noticia-detalle/noticia-detalle.component').then(m => m.NoticiaDetalleComponent) },
  { path: 'donaciones', loadComponent: () => import('./modules/donaciones/donaciones.component').then(m => m.DonacionesComponent) },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  // Main dashboard (role-routing hub)
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  // Parque shared routes
  { path: 'parque/beneficiarios', component: BeneficiaryFormComponent, canActivate: [authGuard] },
  { path: 'parque/servicios', component: ServiciosFormComponent, canActivate: [authGuard] },
  { path: 'indicadores', component: IndicadoresComponent, canActivate: [authGuard] },
  { path: 'seguimiento', component: SeguimientoComponent, canActivate: [authGuard] },
  { path: 'diagnostico-territorial', component: DiagnosticoComponent, canActivate: [authGuard] },
  { path: 'academico', component: AcademicoComponent, canActivate: [authGuard] },
  { path: 'recursos', component: RecursosComponent, canActivate: [authGuard] },
  { path: 'offline-status', component: OfflineStatusComponent, canActivate: [authGuard] },
  { path: 'panel-control', component: PanelControlComponent, canActivate: [authGuard] },
  // Docente / Coordinador routes
  { path: 'docente', component: DocenteDashboardComponent, canActivate: [authGuard] },
  { path: 'docente/supervision', component: SupervisionComponent, canActivate: [authGuard] },
  { path: 'docente/gestion-casos', component: GestionCasosComponent, canActivate: [authGuard] },
  { path: 'docente/modulo-academico', component: ModuloAcademicoDocenteComponent, canActivate: [authGuard] },
  { path: 'docente/reportes-academicos', component: ReportesAcademicosComponent, canActivate: [authGuard] },
  // Estudiante routes
  { path: 'estudiante', component: EstudianteDashboardComponent, canActivate: [authGuard] },
  { path: 'estudiante/atencion', component: AtencionComponent, canActivate: [authGuard] },
  { path: 'estudiante/historial', component: HistorialComponent, canActivate: [authGuard] },
  // Beneficiario routes
  { path: 'beneficiario', component: BeneficiarioDashboardComponent, canActivate: [authGuard] },
  { path: 'beneficiario/mis-atenciones', component: MisAtencionesComponent, canActivate: [authGuard] },
  { path: 'beneficiario/seguimiento-caso', component: SeguimientoCasoBeneficiarioComponent, canActivate: [authGuard] },
  { path: 'beneficiario/perfil', component: PerfilBeneficiarioComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
