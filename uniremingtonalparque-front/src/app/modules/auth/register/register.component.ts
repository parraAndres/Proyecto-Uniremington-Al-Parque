import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage = '';
  successMessage = '';



  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      nombreCompleto: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  async onSubmit() {
    if (this.registerForm.invalid) return;

    try {
      this.errorMessage = '';
      this.successMessage = '';
      const formValue = this.registerForm.value;
      
      const newUser: any = {
        email: formValue.email,
        nombreCompleto: formValue.nombreCompleto,
        passwordHash: formValue.password, // Se hasheará en el servicio
        role: 'cliente'
      };

      await this.authService.registerUser(newUser);
      this.successMessage = 'Usuario registrado con éxito. Redirigiendo al login...';
      
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
      
    } catch (error: any) {
      this.errorMessage = error.message || 'Error al registrar usuario';
    }
  }


}
