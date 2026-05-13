import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { db } from '../../core/database/app-database';
import { AuthService } from '../../core/services/auth.service';
import { StatsService, ImpactStats } from '../../core/services/stats.service';
import { UserService } from '../../core/services/user.service';
import { NewsService, Noticia } from '../../core/services/news.service';
import { ToastService } from '../../core/services/toast.service';
import { interval, startWith, switchMap } from 'rxjs';

@Component({
  selector: 'app-panel-control',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './panel-control.component.html',
  styleUrls: ['./panel-control.component.scss']
})
export class PanelControlComponent implements OnInit {
  isAdmin = false;
  successMessage = '';
  errorMessage = '';
  impactStats: ImpactStats = {
    municipiosVisitados: 0,
    personasAtendidas: 0,
    personasActivas: 0,
    personasRegistradas: 0,
    totalAsistencias: 0,
    totalEstudiantes: 0
  };
  activeView: 'dashboard' | 'students' | 'news' = 'dashboard';
  accounts: any[] = [];
  noticias: Noticia[] = [];
  accountForm!: FormGroup;
  newsForm!: FormGroup;

  facultades = [
    'Medicina Veterinaria y Zootecnia',
    'Ciencias Jurídicas',
    'Ciencias Empresariales',
    'Diseño',
    'Salud',
    'Contaduría',
    'Ingeniería'
  ];

  programasPorFacultad: { [key: string]: string[] } = {
    'Medicina Veterinaria y Zootecnia': ['Medicina Veterinaria', 'Zootecnia'],
    'Ciencias Jurídicas': ['Derecho', 'Especialización en Derecho'],
    'Ciencias Empresariales': ['Administración de Empresas', 'Negocios Internacionales'],
    'Diseño': ['Diseño Gráfico', 'Diseño Industrial'],
    'Salud': ['Enfermería', 'Nutrición'],
    'Contaduría': ['Contaduría Pública'],
    'Ingeniería': ['Ingeniería de Sistemas', 'Ingeniería Civil', 'Ingeniería Industrial']
  };

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private statsService: StatsService,
    private userService: UserService,
    private newsService: NewsService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.accountForm = this.fb.group({
      nombreCompleto: ['', Validators.required],
      documento: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      facultad: ['', Validators.required],
      programa: ['', Validators.required],
      rol: ['estudiante', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.newsForm = this.fb.group({
      titulo: ['', Validators.required],
      contenido: ['', Validators.required],
      imageUrl: [''],
      autor: ['Administración']
    });

    this.accountForm.get('facultad')?.valueChanges.subscribe(() => {
      this.accountForm.patchValue({ programa: '' });
    });
  }

  ngOnInit() {
    const currentUser = this.authService.currentUserValue;
    if (currentUser && currentUser.documento === '123456') {
      this.isAdmin = true;
      this.loadAccounts();
      this.loadNews();
      this.startStatsPolling();
    }

    this.authService.currentUser$.subscribe(user => {
      if (user) {
        if (user.documento === '123456') {
          this.isAdmin = true;
          this.loadAccounts();
          this.loadNews();
          this.startStatsPolling();
        } else {
          this.router.navigate(['/dashboard']);
        }
      }
    });
  }

  startStatsPolling() {
    interval(3600000).pipe(
      startWith(0),
      switchMap(() => this.statsService.getImpactStats())
    ).subscribe({
      next: (stats) => this.impactStats = stats,
      error: (err) => console.error('Error fetching admin stats', err)
    });
  }

  get programasDisponibles(): string[] {
    const facultad = this.accountForm.get('facultad')?.value;
    return facultad ? this.programasPorFacultad[facultad] : ['Ej: Ingeniería de Sistemas', 'Ej: Derecho', 'Ej: Medicina Veterinaria'];
  }

  async onRegisterAccount() {
    if (this.accountForm.invalid) return;

    try {
      this.errorMessage = '';
      this.successMessage = '';
      const formValue = this.accountForm.value;
      
      const newUser: any = {
        email: formValue.email,
        documento: formValue.documento,
        nombreCompleto: formValue.nombreCompleto,
        facultad: formValue.facultad,
        programa: formValue.programa,
        password: formValue.password,
        rol: formValue.rol
      };

      await this.authService.registerUser(newUser);

      this.successMessage = 'La cuenta ha sido creada';
      
      // Efecto WOW: Notificación de creación de cuenta y asignación a facultad
      this.toastService.show(
        'Nueva Cuenta Registrada', 
        `Se ha registrado el ${formValue.rol} ${formValue.nombreCompleto} para la facultad de ${formValue.facultad}.`, 
        'info'
      );

      this.accountForm.reset({ rol: 'estudiante' });
      this.loadAccounts();
      
      setTimeout(() => this.successMessage = '', 3000);
      
    } catch (error: any) {
      this.errorMessage = error.message || 'Error al registrar la cuenta';
    }
  }

  loadAccounts() {
    this.userService.getUsers().subscribe({
      next: (users: any[]) => {
        // El backend guarda los roles en mayúsculas
        this.accounts = users.filter((u: any) => 
          u.rol === 'ESTUDIANTE' || 
          u.rol === 'PROFESOR' || 
          u.rol === 'estudiante' || 
          u.rol === 'profesor'
        );
      },
      error: (err: any) => console.error('Error al cargar cuentas del servidor', err)
    });
  }

  deleteAccount(identifier: string) {
    if (confirm(`¿Estás seguro de que deseas eliminar la cuenta ${identifier}?`)) {
      this.userService.deleteUser(identifier).subscribe({
        next: () => {
          this.successMessage = `Cuenta ${identifier} eliminada correctamente.`;
          this.loadAccounts();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (err: any) => {
          this.errorMessage = 'Error al eliminar la cuenta del servidor';
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
        this.successMessage = 'Noticia publicada correctamente';
        
        // Efecto WOW: Notificación a toda la comunidad estudiantil de la nueva noticia
        this.toastService.show(
          '¡Nueva Noticia Publicada!', 
          `Se acaba de publicar: "${this.newsForm.value.titulo}". Notificando a todos los usuarios...`, 
          'success'
        );

        this.newsForm.reset({ autor: 'Administración' });
        this.loadNews();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => this.errorMessage = 'Error al publicar la noticia'
    });
  }

  deleteNoticia(id: string) {
    if (confirm('¿Estás seguro de eliminar esta noticia?')) {
      this.newsService.deleteNoticia(id).subscribe({
        next: () => {
          this.successMessage = 'Noticia eliminada';
          this.loadNews();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: () => this.errorMessage = 'Error al eliminar la noticia'
      });
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
