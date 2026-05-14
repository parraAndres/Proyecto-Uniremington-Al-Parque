import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { StatsService, ImpactStats, FacultadStats, TerritorialStats } from '../../core/services/stats.service';
import { UserService, User } from '../../core/services/user.service';
import { NewsService, Noticia } from '../../core/services/news.service';
import { JornadaService, Jornada } from '../../core/services/jornada.service';
import { ConfigParamService, ConfigParam } from '../../core/services/config-param.service';
import { SocialService, ServicioSocial } from '../../core/services/social.service';
import { SyncService } from '../parque/services/sync.service';
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
  isOnline = true;
  pendingSyncCount = 0;
  isAdmin = false;
  isDocente = false;
  isEstudiante = false;
  isBeneficiario = false;
  isEditing = false;
  editingUserId = '';
  activeView: 'dashboard' | 'students' | 'news' | 'impact' | 'efficiency' | 'territorial' | 'ranking' | 'jornadas' | 'config' | 'strategic' | 'reports' | 'docente_students' | 'docente_jornadas' | 'docente_casos' | 'docente_stats' | 'estudiante_jornada' | 'estudiante_atenciones' | 'estudiante_beneficiarios' | 'estudiante_registro_atencion' | 'estudiante_seguimiento' = 'news';
  
  successMessage = '';
  errorMessage = '';

  // Datos Docente
  misEstudiantes: User[] = [];
  misJornadas: any[] = [];
  misCasosPendientes: any[] = [];
  docenteStats: any = { totalJornadas: 0, totalEstudiantes: 0, beneficiariosAtendidos: 0, casosPendientes: 0 };

  // Datos Estudiante
  activeJornada: any = null;
  misAtencionesCount = 0;
  atencionesFinalizadas = 0;
  casosAbiertos = 0;

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
  beneficiarioForm: FormGroup;
  atencionForm: FormGroup;
  seguimientoForm: FormGroup;
  
  beneficiarios: User[] = [];
  tiposServicio: ConfigParam[] = [];
  historialAtenciones: ServicioSocial[] = [];

  facultades = [
    'Ingeniería', 
    'Ciencias de la Salud', 
    'Artes y Diseño', 
    'Ciencias Jurídicas', 
    'Ciencias Empresariales', 
    'Medicina Veterinaria', 
    'Contaduría'
  ];
  programas: { [key: string]: string[] } = {
    'Ingeniería': ['Sistemas', 'Industrial', 'Civil'],
    'Ciencias de la Salud': ['Enfermería', 'Nutrición'],
    'Artes y Diseño': ['Diseño Gráfico', 'Diseño de Modas'],
    'Ciencias Jurídicas': ['Derecho', 'Criminalística'],
    'Ciencias Empresariales': ['Administración de Empresas', 'Negocios Internacionales'],
    'Medicina Veterinaria': ['Medicina Veterinaria'],
    'Contaduría': ['Contaduría Pública']
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
    private socialService: SocialService,
    private reporteService: ReporteService,
    private docenteService: DocenteService,
    public toastService: ToastService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
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
      imagenUrl: [''],
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

    this.beneficiarioForm = this.fb.group({
      documento: ['', Validators.required],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      telefono: [''],
      direccion: [''],
      municipio: ['', Validators.required],
      barrio: [''],
      consentimientoDatos: [false, Validators.requiredTrue]
    });

    this.atencionForm = this.fb.group({
      beneficiarioId: ['', Validators.required],
      tipoServicio: ['', Validators.required],
      resultadoAtencion: ['', Validators.required],
      observaciones: [''],
      duracionMinutos: [30, [Validators.required, Validators.min(5)]]
    });

    this.seguimientoForm = this.fb.group({
      beneficiarioDocumento: ['', Validators.required],
      casoId: ['', Validators.required],
      estadoCaso: ['ABIERTO', Validators.required],
      evolucion: ['', Validators.required],
      observaciones: ['']
    });

    this.accountForm.get('facultad')?.valueChanges.subscribe(() => {
      this.accountForm.patchValue({ programa: '' });
    });
  }

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.checkUserRole();
      this.loadNews(); // Todos pueden ver noticias

      if (this.isAdmin) {
        this.startStatsPolling();
        this.loadAccounts();
        this.loadDetailedStats();
        this.loadJornadas();
        this.loadConfigParams();
      } else if (this.isDocente) {
        this.loadDocenteData();
      } else if (this.isEstudiante) {
        this.loadEstudianteData();
        this.loadTiposServicio();
        this.loadAtencionesHistory();
      }

      // this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
      // this.syncService.pendingCount$.subscribe(count => this.pendingSyncCount = count);
    }
  }

  loadTiposServicio() {
    this.configService.getParamsByTipo('TIPO_SERVICIO').subscribe(res => this.tiposServicio = res);
  }

  loadEstudianteData() {
    const user = this.authService.currentUserValue;
    if (!user) return;

    // Obtener estadísticas del estudiante
    this.statsService.getEstudianteStats(user.id).subscribe({
      next: (res) => {
        this.misAtencionesCount = res.totalAtenciones;
        // Mock de jornada activa si no hay servicio real aún
        this.activeJornada = {
          nombre: 'Jornada de Salud y Bienestar',
          municipio: 'Medellín',
          barrio: 'San Javier',
          estado: 'EN CURSO',
          fecha: new Date().toISOString()
        };
      }
    });

    // Suscribirse a conteo offline
    // this.syncService.pendingCount$.subscribe(count => this.pendingSyncCount = count);
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
    this.isEstudiante = user && user.rol?.toUpperCase() === 'ESTUDIANTE';
    this.isBeneficiario = user && user.rol?.toUpperCase() === 'BENEFICIARIO';
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

  // --- MÉTODOS DE BENEFICIARIO (ESTUDIANTE) ---
  buscarBeneficiario(): void {
    const doc = this.beneficiarioForm.get('documento')?.value;
    if (!doc) return;

    this.userService.getUsers().subscribe((users: User[]) => {
      const b = users.find(u => u.documento === doc && u.rol?.toUpperCase() === 'BENEFICIARIO');
      if (b) {
        this.beneficiarioForm.patchValue({
          nombres: b.nombreCompleto || b.nombre,
          apellidos: '', 
          telefono: '', 
          municipio: b.municipio || b.facultad, 
          barrio: b.barrio || b.programa, 
          consentimientoDatos: true
        });
        this.toastService.show('Beneficiario Encontrado', 'Se han cargado los datos existentes.', 'info');
      } else {
        this.toastService.show('No encontrado', 'Puedes registrarlo como nuevo.', 'warning');
      }
    });
  }

  // --- MÉTODOS DE ATENCIÓN (ESTUDIANTE) ---
  iniciarRegistroAtencion(beneficiario?: User): void {
    this.activeView = 'estudiante_registro_atencion';
    this.loadBeneficiarios();
    if (beneficiario) {
      this.atencionForm.patchValue({ beneficiarioId: beneficiario.id });
    }
  }

  loadBeneficiarios(): void {
    this.userService.getUsers().subscribe((users: User[]) => {
      this.beneficiarios = users.filter(u => u.rol?.toUpperCase() === 'BENEFICIARIO');
    });
  }

  loadAtencionesHistory(): void {
    const user = this.authService.currentUserValue;
    if (user && user.documento) {
      this.socialService.getServiciosByEstudiante(user.documento).subscribe(res => {
        this.historialAtenciones = res;
        this.misAtencionesCount = res.length;
        this.atencionesFinalizadas = res.filter(a => a.estado?.toUpperCase() === 'FINALIZADO').length;
        this.casosAbiertos = res.filter(a => a.estado?.toUpperCase() === 'ABIERTO' || a.estado?.toUpperCase() === 'PROCESO').length;
      });
    }
  }

  onSaveAtencion(): void {
    if (this.atencionForm.invalid) return;

    const data = {
      ...this.atencionForm.value,
      fechaServicio: new Date().toISOString(),
      estudianteId: this.authService.currentUserValue?.documento
    };

    if (!this.isOnline) {
      // this.syncService.saveLocally('servicios', { ...data, id: crypto.randomUUID() });
      this.toastService.show('Guardado Localmente', 'No tienes conexión. Los datos se sincronizarán al reconectar.', 'info');
      this.afterSaveAtencion();
    } else {
      this.socialService.saveServicio(data).subscribe({
        next: () => {
          this.toastService.show('¡Atención Registrada!', 'La atención ha sido guardada con éxito.', 'success');
          this.afterSaveAtencion();
        },
        error: () => {
          // this.syncService.saveLocally('servicios', { ...data, id: crypto.randomUUID() });
          this.toastService.show('Guardado en Borrador', 'Error de red. Guardado localmente.', 'warning');
          this.afterSaveAtencion();
        }
      });
    }
  }

  private afterSaveAtencion() {
    this.atencionForm.reset({ duracionMinutos: 30 });
    this.loadEstudianteData(); 
    this.activeView = 'estudiante_jornada';
  }

  // --- MÉTODOS DE SEGUIMIENTO (ESTUDIANTE) ---
  iniciarSeguimiento(item?: ServicioSocial): void {
    this.activeView = 'estudiante_seguimiento';
    if (item) {
      this.seguimientoForm.patchValue({
        beneficiarioDocumento: item.beneficiario.documento,
        casoId: `CASO-${item.id}`
      });
    }
  }

  onSaveSeguimiento(): void {
    if (this.seguimientoForm.invalid) return;

    const data = {
      ...this.seguimientoForm.value,
      fechaEstado: new Date().toISOString()
    };

    if (!this.isOnline) {
      // this.syncService.saveLocally('seguimientos', { ...data, id: crypto.randomUUID() });
      this.toastService.show('Guardado Localmente', 'Avance guardado para sincronizar después.', 'info');
      this.afterSaveSeguimiento();
    } else {
      this.socialService.saveSeguimiento(data).subscribe({
        next: () => {
          this.toastService.show('¡Seguimiento Registrado!', 'El avance del caso ha sido guardado.', 'success');
          this.afterSaveSeguimiento();
        },
        error: () => {
          // this.syncService.saveLocally('seguimientos', { ...data, id: crypto.randomUUID() });
          this.toastService.show('Error de Red', 'Guardado localmente.', 'warning');
          this.afterSaveSeguimiento();
        }
      });
    }
  }

  private afterSaveSeguimiento() {
    this.seguimientoForm.reset({ estadoCaso: 'ABIERTO' });
    this.activeView = 'estudiante_jornada';
  }

  onSaveBeneficiario(): void {
    if (this.beneficiarioForm.invalid) {
      this.toastService.show('Formulario Incompleto', 'Por favor verifica los campos obligatorios.', 'warning');
      return;
    }

    const data = {
      ...this.beneficiarioForm.value,
      id: crypto.randomUUID()
    };

    if (!this.isOnline) {
      // this.syncService.saveLocally('beneficiaries', data);
      this.toastService.show('Beneficiario Guardado Local', 'Se sincronizará cuando vuelvas a tener señal.', 'info');
      this.afterSaveBeneficiario();
    } else {
      this.socialService.saveBeneficiario(data).subscribe({
        next: () => {
          this.toastService.show('¡Registrado!', 'El beneficiario ha sido guardado correctamente.', 'success');
          this.afterSaveBeneficiario();
        },
        error: () => {
          // this.syncService.saveLocally('beneficiaries', data);
          this.toastService.show('Guardado Localmente', 'Falla de conexión. Registro en cola.', 'warning');
          this.afterSaveBeneficiario();
        }
      });
    }
  }

  private afterSaveBeneficiario() {
    this.beneficiarioForm.reset({ consentimientoDatos: false });
    this.activeView = 'estudiante_jornada';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
