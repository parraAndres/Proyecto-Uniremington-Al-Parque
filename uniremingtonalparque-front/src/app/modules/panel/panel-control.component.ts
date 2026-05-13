import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { StatsService, ImpactStats, FacultadStats, TerritorialStats } from '../../core/services/stats.service';
import { UserService, User } from '../../core/services/user.service';
import { NewsService, Noticia } from '../../core/services/news.service';
import { JornadaService, Jornada } from '../../core/services/jornada.service';
import { ConfigParamService, ConfigParam } from '../../core/services/config-param.service';
import { ReporteService } from '../../core/services/reporte.service';
import { DocenteService } from '../../core/services/docente.service';
import { ToastService } from '../../core/services/toast.service';
import { interval, startWith, switchMap } from 'rxjs';

@Component({
  selector: 'app-panel-control',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './panel-control.component.html',
  styleUrls: ['./panel-control.component.scss']
})
export class PanelControlComponent implements OnInit {
  isAdmin = false;
  isDocente = false;
  isEditing = false;
  editingUserId = '';
  activeView: 'dashboard' | 'students' | 'news' | 'impact' | 'efficiency' | 'territorial' | 'ranking' | 'jornadas' | 'config' | 'strategic' | 'reports' | 'docente_students' | 'docente_jornadas' | 'docente_casos' = 'dashboard';
  
  successMessage = '';
  errorMessage = '';

  // Datos Docente
  misEstudiantes: User[] = [];
  misJornadas: any[] = [];
  misCasosPendientes: any[] = [];
  docenteStats: any = { totalJornadas: 0, totalEstudiantes: 0, beneficiariosAtendidos: 0, casosPendientes: 0 };

  // Filtros Reportes
  reportFilters = {
    inicio: '',
    fin: '',
    municipio: '',
    vereda: ''
  };

  // Configuración
  selectedConfigType: 'FACULTAD' | 'TIPO_SERVICIO' | 'CATEGORIA' | 'PROBLEMATICA' | 'ESTADO_CASO' = 'FACULTAD';
  configParams: ConfigParam[] = [];

  impactStats: ImpactStats = {
    personasRegistradas: 0,
    personasActivas: 0,
    municipiosVisitados: 0,
    personasAtendidas: 0,
    totalAsistencias: 0,
    totalEstudiantes: 0,
    inversionSocialEstimada: 0
  };

  facultadStats: FacultadStats[] = [];
  territorialStats: TerritorialStats[] = [];
  coberturaStats: any[] = [];
  casosStats: any = { total: 0, abiertos: 0, cerrados: 0, porcentajeResolucion: 0 };
  efficiencyMetrics: any = {};
  rankingEstudiantes: any[] = [];
  jornadas: Jornada[] = [];

  accounts: User[] = [];
  noticias: Noticia[] = [];
  
  accountForm: FormGroup;
  newsForm: FormGroup;
  jornadaForm: FormGroup;
  configForm: FormGroup;
  reportForm: FormGroup;

  facultades = ['Ingeniería', 'Ciencias de la Salud', 'Ciencias Jurídicas', 'Ciencias Empresariales', 'Diseño'];
  programas: { [key: string]: string[] } = {
    'Ingeniería': ['Sistemas', 'Industrial', 'Civil'],
    'Ciencias de la Salud': ['Medicina Veterinaria', 'Enfermería', 'Nutrición'],
    'Ciencias Jurídicas': ['Derecho', 'Criminalística'],
    'Ciencias Empresariales': ['Contaduría', 'Administración de Empresas'],
    'Diseño': ['Diseño Gráfico', 'Diseño de Modas']
  };

  get programasDisponibles() {
    const facultad = this.accountForm.get('facultad')?.value;
    return facultad ? this.programas[facultad] || [] : [];
  }

  get docentesActivos() {
    return this.accounts.filter(u => u.rol?.toUpperCase() === 'PROFESOR');
  }

  get estudiantesActivos() {
    return this.accounts.filter(u => u.rol?.toUpperCase() === 'ESTUDIANTE');
  }

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private statsService: StatsService,
    private userService: UserService,
    private newsService: NewsService,
    private jornadaService: JornadaService,
    private configService: ConfigParamService,
    private reporteService: ReporteService,
    private docenteService: DocenteService,
    public toastService: ToastService,
    private router: Router
  ) {
    this.accountForm = this.fb.group({
      nombreCompleto: ['', Validators.required],
      documento: ['', Validators.required],
      email: ['', [Validators.required]],
      facultad: ['', Validators.required],
      programa: ['', Validators.required],
      rol: ['estudiante', Validators.required],
      genero: ['otro', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.newsForm = this.fb.group({
      titulo: ['', Validators.required],
      contenido: ['', Validators.required],
      imageUrl: [''],
      autor: ['Administración']
    });

    this.jornadaForm = this.fb.group({
      nombre: ['', Validators.required],
      fecha: ['', Validators.required],
      municipio: ['', Validators.required],
      vereda: [''],
      barrio: [''],
      descripcion: [''],
      estado: ['PROGRAMADA']
    });

    this.configForm = this.fb.group({
      valor: ['', Validators.required],
      descripcion: ['']
    });

    this.reportForm = this.fb.group({
      inicio: [''],
      fin: [''],
      municipio: [''],
      vereda: [''],
      barrio: ['']
    });

    this.accountForm.get('facultad')?.valueChanges.subscribe(() => {
      this.accountForm.patchValue({ programa: '' });
    });
  }

  ngOnInit() {
    this.checkUserRole();
    if (this.isAdmin) {
      this.startStatsPolling();
      this.loadAccounts();
      this.loadNews();
      this.loadDetailedStats();
      this.loadJornadas();
      this.loadConfigParams();
    } else if (this.isDocente) {
      this.loadDocenteData();
    }
  }

  // --- MÉTODOS DE REPORTES ---
  exportExcel() {
    this.reporteService.downloadExcel(this.reportForm.value);
  }

  exportPdf() {
    this.reporteService.downloadPdf(this.reportForm.value);
  }

  checkUserRole() {
    const user = this.authService.currentUserValue;
    this.isAdmin = user && user.rol?.toUpperCase() === 'ADMIN';
    this.isDocente = user && (user.rol?.toUpperCase() === 'PROFESOR' || user.rol?.toUpperCase() === 'DOCENTE');
  }

  loadDocenteData() {
    const user = this.authService.currentUserValue;
    if (!user) return;

    this.docenteService.getStats(user.id).subscribe(res => this.docenteStats = res);
    this.docenteService.getMisEstudiantes(user.id).subscribe(res => this.misEstudiantes = res);
    this.docenteService.getMisJornadas(user.id).subscribe(res => this.misJornadas = res);
    this.docenteService.getCasosPendientes(user.id).subscribe(res => this.misCasosPendientes = res);
  }

  startStatsPolling() {
    interval(10000).pipe(
      startWith(0),
      switchMap(() => this.statsService.getImpactStats())
    ).subscribe({
      next: (stats) => this.impactStats = stats,
      error: (err) => console.error('Error polling stats', err)
    });
  }

  loadDetailedStats() {
    this.statsService.getFacultadStats().subscribe(res => this.facultadStats = res);
    this.statsService.getTerritorialStats().subscribe(res => this.territorialStats = res);
    this.statsService.getEfficiencyMetrics().subscribe(res => this.efficiencyMetrics = res);
    this.statsService.getRankingEstudiantes().subscribe(res => this.rankingEstudiantes = res);
    
    // Nuevas métricas estratégicas
    this.statsService.getResumenEstrategico().subscribe((res: any) => {
      this.coberturaStats = res.cobertura;
      this.casosStats = res.casos;
    });
  }

  loadJornadas() {
    this.jornadaService.getJornadas().subscribe(res => this.jornadas = res);
  }

  // --- MÉTODOS DE CONFIGURACIÓN ---
  changeConfigType(type: any) {
    this.selectedConfigType = type;
    this.loadConfigParams();
  }

  loadConfigParams() {
    this.configService.getParamsByTipo(this.selectedConfigType).subscribe(res => this.configParams = res);
  }

  onSaveConfig() {
    if (this.configForm.invalid) return;
    const param: ConfigParam = {
      ...this.configForm.value,
      tipo: this.selectedConfigType,
      activo: true
    };
    this.configService.saveParam(param).subscribe({
      next: () => {
        this.toastService.show('Guardado', 'Parámetro guardado correctamente.', 'success');
        this.configForm.reset();
        this.loadConfigParams();
      }
    });
  }

  deleteConfig(id: number) {
    if (confirm('¿Eliminar este parámetro?')) {
      this.configService.deleteParam(id).subscribe(() => this.loadConfigParams());
    }
  }

  // --- MÉTODOS DE JORNADA ---
  onCreateJornada() {
    if (this.jornadaForm.invalid) return;
    this.jornadaService.createJornada(this.jornadaForm.value).subscribe({
      next: () => {
        this.toastService.show('¡Jornada Creada!', 'La nueva jornada ha sido registrada con éxito.', 'success');
        this.jornadaForm.reset({ estado: 'PROGRAMADA' });
        this.loadJornadas();
      }
    });
  }

  asignarA_Jornada(jornadaId: number, usuarioId: string) {
    this.jornadaService.asignarPersonal(jornadaId, usuarioId).subscribe({
      next: () => {
        this.toastService.show('Personal Asignado', 'El docente/estudiante ha sido añadido a la jornada.', 'success');
        this.loadJornadas();
      }
    });
  }

  quitarDe_Jornada(jornadaId: number, usuarioId: string) {
    this.jornadaService.quitarPersonal(jornadaId, usuarioId).subscribe({
      next: () => {
        this.loadJornadas();
      }
    });
  }

  deleteJornada(id: number) {
    if (confirm('¿Deseas eliminar esta jornada?')) {
      this.jornadaService.deleteJornada(id).subscribe({
        next: () => this.loadJornadas()
      });
    }
  }

  // --- MÉTODOS DE CUENTA ---
  async onRegisterAccount() {
    if (this.accountForm.invalid) return;
    try {
      this.errorMessage = '';
      this.successMessage = '';
      const formValue = this.accountForm.value;
      const userData: any = {
        email: formValue.email,
        documento: formValue.documento,
        nombreCompleto: formValue.nombreCompleto,
        facultad: formValue.facultad,
        programa: formValue.programa,
        password: formValue.password,
        rol: formValue.rol,
        genero: formValue.genero,
        activo: true
      };
      if (this.isEditing) {
        this.userService.updateUser(this.editingUserId, userData).subscribe({
          next: () => this.handleActionSuccess('Usuario actualizado correctamente', 'Usuario Actualizado'),
          error: (err) => this.errorMessage = 'Error al actualizar usuario'
        });
      } else {
        await this.authService.registerUser(userData);
        this.handleActionSuccess('La cuenta ha sido creada', 'Nueva Cuenta Registrada');
      }
    } catch (error: any) {
      this.errorMessage = error.message || 'Error en la operación';
    }
  }

  private handleActionSuccess(msg: string, title: string) {
    this.successMessage = msg;
    this.toastService.show(title, msg, 'success');
    this.cancelEdit();
    this.loadAccounts();
    setTimeout(() => this.successMessage = '', 3000);
  }

  editUser(user: any) {
    this.isEditing = true;
    this.editingUserId = user.id;
    this.accountForm.patchValue({
      nombreCompleto: user.nombreCompleto || user.nombre,
      email: user.email,
      documento: user.documento,
      facultad: user.facultad,
      programa: user.programa,
      rol: user.rol?.toLowerCase(),
      genero: user.genero?.toLowerCase(),
      password: 'password_placeholder'
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit() {
    this.isEditing = false;
    this.editingUserId = '';
    this.accountForm.reset({ rol: 'estudiante', genero: 'otro' });
  }

  toggleBlock(user: any) {
    this.userService.toggleUserStatus(user.id).subscribe({
      next: () => {
        this.toastService.show(
          user.activo ? 'Usuario Bloqueado' : 'Usuario Desbloqueado',
          `El usuario ${user.nombreCompleto || user.nombre} ha sido ${user.activo ? 'bloqueado' : 'desbloqueado'}.`,
          user.activo ? 'warning' : 'info'
        );
        this.loadAccounts();
      }
    });
  }

  loadAccounts() {
    this.userService.getUsers().subscribe({
      next: (users: any[]) => {
        this.accounts = users.filter((u: any) => {
          const rol = u.rol?.toUpperCase();
          return rol === 'ESTUDIANTE' || rol === 'PROFESOR' || rol === 'ADMIN' || rol === 'BENEFICIARIO';
        });
      },
      error: (err: any) => console.error('Error al cargar cuentas', err)
    });
  }

  deleteAccount(identifier: string) {
    if (confirm(`¿Estás seguro de que deseas eliminar la cuenta ${identifier}?`)) {
      this.userService.deleteUser(identifier).subscribe({
        next: () => {
          this.successMessage = `Cuenta ${identifier} eliminada correctamente.`;
          this.loadAccounts();
          setTimeout(() => this.successMessage = '', 3000);
        }
      });
    }
  }

  loadNews() {
    this.newsService.getNoticias().subscribe({
      next: (res) => this.noticias = res,
      error: (err) => console.error('Error loading news', err)
    });
  }

  onCreateNews() {
    if (this.newsForm.invalid) return;
    this.newsService.createNoticia(this.newsForm.value).subscribe({
      next: () => {
        this.toastService.show('¡Noticia Publicada!', 'Se ha publicado la noticia correctamente.', 'success');
        this.newsForm.reset({ autor: 'Administración' });
        this.loadNews();
      }
    });
  }

  deleteNoticia(id: string) {
    if (confirm('¿Estás seguro de eliminar esta noticia?')) {
      this.newsService.deleteNoticia(id).subscribe({
        next: () => {
          this.loadNews();
        }
      });
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
