import { Routes } from '@angular/router';
import { LandingComponent } from './modules/landing/landing.component';
import { LoginComponent } from './modules/auth/login/login.component';
import { RegisterComponent } from './modules/auth/register/register.component';
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

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'parque/beneficiarios', component: BeneficiaryFormComponent, canActivate: [authGuard] },
  { path: 'parque/servicios', component: ServiciosFormComponent, canActivate: [authGuard] },
  { path: 'indicadores', component: IndicadoresComponent, canActivate: [authGuard] },
  { path: 'seguimiento', component: SeguimientoComponent, canActivate: [authGuard] },
  { path: 'diagnostico-territorial', component: DiagnosticoComponent, canActivate: [authGuard] },
  { path: 'academico', component: AcademicoComponent, canActivate: [authGuard] },
  { path: 'recursos', component: RecursosComponent, canActivate: [authGuard] },
  { path: 'offline-status', component: OfflineStatusComponent, canActivate: [authGuard] },
  { path: 'panel-control', component: PanelControlComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
