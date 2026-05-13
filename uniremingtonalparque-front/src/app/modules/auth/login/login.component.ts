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
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      documento: ['', [Validators.required]],
      password: ['', Validators.required]
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
      const { documento, password } = this.loginForm.value;
      const user = await firstValueFrom(this.authService.login(documento, password));
      
      if (documento === '123456' && password === '123456') {
        this.router.navigate(['/panel-control']);
      } else {
        this.router.navigate(['/dashboard']);
      }
    } catch (error: any) {
      if (error.error && typeof error.error === 'object' && error.error.message) {
        this.errorMessage = error.error.message;
      } else if (error.error && typeof error.error === 'string') {
        this.errorMessage = error.error;
      } else {
        this.errorMessage = 'Credenciales incorrectas o error de servidor';
      }
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
