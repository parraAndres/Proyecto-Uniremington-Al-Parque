import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage = '';
  successMessage = '';
  isLoading = false;
  isForgotPasswordMode = false;
  showPassword = false;
  forgotPasswordForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      documento: ['', [Validators.required]],
      password: ['', Validators.required]
    });

    this.forgotPasswordForm = this.fb.group({
      identificador: ['', [Validators.required]]
    });
  }

  async onSubmit() {
    if (this.loginForm.invalid) {
      this.markFormGroupTouched(this.loginForm);
      return;
    }

    try {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      const { documento, password } = this.loginForm.value;
      const user = await firstValueFrom(this.authService.login(documento, password));
      
      if (user.rol?.toUpperCase() === 'ADMIN' || (documento === '123456' && password === '123456')) {
        this.router.navigate(['/panel-control']);
      } else {
        this.router.navigate(['/panel-control']); // Todos van al mismo panel ahora
      }
    } catch (error: any) {
      this.errorMessage = this.extractError(error);
    } finally {
      this.isLoading = false;
    }
  }

  async onForgotPasswordSubmit() {
    if (this.forgotPasswordForm.invalid) {
      return;
    }

    try {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      const { identificador } = this.forgotPasswordForm.value;
      const res = await firstValueFrom(this.authService.forgotPassword(identificador));
      this.successMessage = res.message;
      this.isForgotPasswordMode = false;
    } catch (error: any) {
      this.errorMessage = this.extractError(error);
    } finally {
      this.isLoading = false;
    }
  }

  toggleForgotPasswordMode() {
    this.isForgotPasswordMode = !this.isForgotPasswordMode;
    this.errorMessage = '';
    this.successMessage = '';
  }

  private extractError(error: any): string {
    if (error.error && typeof error.error === 'object' && error.error.message) {
      return error.error.message;
    } else if (error.error && typeof error.error === 'string') {
      return error.error;
    } else {
      return 'Ocurrió un error inesperado';
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
