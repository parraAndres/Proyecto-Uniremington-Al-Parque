import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { db } from '../../core/database/app-database';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-panel-control',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './panel-control.component.html',
  styles: [`
    .dashboard-charts { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem; margin-top: 1.5rem; }
    .chart-card { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
    .chart-card h3 { margin-top: 0; color: var(--primary-color, #004d99); font-size: 1.1rem; border-bottom: 2px solid #eee; padding-bottom: 0.5rem; margin-bottom: 1rem; }
    .canvas-placeholder { width: 100%; height: 250px; background-color: #f9f9f9; display: flex; align-items: center; justify-content: center; }
  `]
})
export class PanelControlComponent implements OnInit {
  students: any[] = [];
  studentForm: FormGroup;
  isAdmin = false;
  successMessage = '';
  errorMessage = '';

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
    const currentUser = this.authService['currentUserSubject']?.value;
    if (currentUser && currentUser.role === 'admin') {
      this.isAdmin = true;
      this.loadStudents();
    }

    // También suscribirse por si el usuario se autentica después
    this.authService.currentUser$.subscribe(user => {
      if (user && user.role === 'admin') {
        this.isAdmin = true;
        this.loadStudents();
      } else if (user && user.role !== 'admin') {
        this.router.navigate(['/dashboard']);
      }
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
}
