import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { db } from '../../core/database/app-database';
import { AuthService } from '../../core/services/auth.service';
import { StatsService, ImpactStats } from '../../core/services/stats.service';
import { interval, startWith, switchMap } from 'rxjs';

@Component({
  selector: 'app-panel-control',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './panel-control.component.html',
  styleUrls: ['./panel-control.component.scss']
})
export class PanelControlComponent implements OnInit {
  students: any[] = [];
  studentForm: FormGroup;
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
    private router: Router
  ) {
    this.studentForm = this.fb.group({
      nombreCompleto: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      facultad: ['', Validators.required],
      programa: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    // Cambiar los programas disponibles cuando cambie la facultad
    this.studentForm.get('facultad')?.valueChanges.subscribe(() => {
      this.studentForm.patchValue({ programa: '' });
    });
  }

  ngOnInit() {
    // Verificar si ya hay un usuario activo al momento de cargar el componente
    const currentUser = this.authService.currentUserValue;
    if (currentUser && currentUser.documento === '123456') {
      this.isAdmin = true;
      this.loadStudents();
      this.startStatsPolling();
    }

    // Suscribirse para cambios en el estado de autenticación
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        if (user.documento === '123456') {
          this.isAdmin = true;
          this.loadStudents();
          // Asegurarse de que el polling solo inicie una vez si no estaba ya activo
          // startStatsPolling ya maneja el estado interno si es necesario, 
          // o podemos simplemente llamarlo aquí.
          this.startStatsPolling();
        } else {
          // Si no es admin, redirigir al dashboard
          this.router.navigate(['/dashboard']);
        }
      }
    });
  }

  startStatsPolling() {
    // Actualización cada 1 hora (3600000 ms) para optimización
    interval(3600000).pipe(
      startWith(0),
      switchMap(() => this.statsService.getImpactStats())
    ).subscribe({
      next: (stats) => this.impactStats = stats,
      error: (err) => console.error('Error fetching admin stats', err)
    });
  }

  get programasDisponibles(): string[] {
    const facultad = this.studentForm.get('facultad')?.value;
    return facultad ? this.programasPorFacultad[facultad] : ['Ej: Ingeniería de Sistemas', 'Ej: Derecho', 'Ej: Medicina Veterinaria'];
  }

  async onRegisterStudent() {
    if (this.studentForm.invalid) return;

    try {
      this.errorMessage = '';
      this.successMessage = '';
      const formValue = this.studentForm.value;
      
      const newUser: any = {
        email: formValue.email,
        nombreCompleto: formValue.nombreCompleto,
        facultad: formValue.facultad,
        programa: formValue.programa,
        passwordHash: formValue.password, // Se hasheará en el servicio
        role: 'estudiante'
      };

      await this.authService.registerUser(newUser);
      this.successMessage = 'Estudiante registrado con éxito.';
      this.studentForm.reset();
      this.loadStudents();
      
      setTimeout(() => {
        this.successMessage = '';
      }, 3000);
      
    } catch (error: any) {
      this.errorMessage = error.message || 'Error al registrar estudiante';
    }
  }

  async loadStudents() {
    try {
      const allUsers = await db.users.toArray();
      this.students = allUsers.filter(u => u.role === 'estudiante');
    } catch (error) {
      console.error('Error al cargar estudiantes', error);
    }
  }

  async deleteStudent(email: string) {
    if (confirm(`¿Estás seguro de que deseas eliminar al estudiante con correo ${email}?`)) {
      try {
        await db.users.delete(email);
        this.successMessage = `Estudiante ${email} eliminado correctamente.`;
        this.loadStudents();
        setTimeout(() => this.successMessage = '', 3000);
      } catch (error: any) {
        this.errorMessage = 'Error al eliminar el estudiante';
      }
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
