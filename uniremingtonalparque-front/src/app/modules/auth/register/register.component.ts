import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  facultades = [
    'Ingeniería',
    'Salud',
    'Artes y Diseño',
    'Ciencias Jurídicas',
    'Empresariales',
    'Veterinaria',
    'Contaduría'
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  async onSubmit() {
    if (this.registerForm.invalid) {
      this.markFormGroupTouched(this.registerForm);
      return;
    }

    try {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      
      const formValue = this.registerForm.value;
      
      const newUser = {
        email: formValue.email,
        password: formValue.password
      };

      await this.authService.registerUser(newUser);
      
      this.successMessage = '¡Registro exitoso! Redirigiendo al inicio de sesión...';
      
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
      
    } catch (error: any) {
      this.errorMessage = error.message || 'Error al registrar usuario';
    } finally {
      this.isLoading = false;
    }
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if ((control as any).controls) {
        this.markFormGroupTouched(control as FormGroup);
      }
    });
  }
}
