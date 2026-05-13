import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { db } from '../../core/database/app-database';
import { AuthService } from '../../core/services/auth.service';
import { StatsService, ImpactStats } from '../../core/services/stats.service';
import { UserService } from '../../core/services/user.service';
import { interval, startWith, switchMap } from 'rxjs';

@Component({
  selector: 'app-panel-control',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './panel-control.component.html',
  styleUrls: ['./panel-control.component.scss']
})
export class PanelControlComponent implements OnInit {
  accounts: any[] = [];
  accountForm: FormGroup;
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
  activeView: 'dashboard' | 'students' = 'dashboard';

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

    this.accountForm.get('facultad')?.valueChanges.subscribe(() => {
      this.accountForm.patchValue({ programa: '' });
    });
  }

  ngOnInit() {
    const currentUser = this.authService.currentUserValue;
    if (currentUser && currentUser.documento === '123456') {
      this.isAdmin = true;
      this.loadAccounts();
      this.startStatsPolling();
    }

    this.authService.currentUser$.subscribe(user => {
      if (user) {
        if (user.documento === '123456') {
          this.isAdmin = true;
          this.loadAccounts();
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


  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
