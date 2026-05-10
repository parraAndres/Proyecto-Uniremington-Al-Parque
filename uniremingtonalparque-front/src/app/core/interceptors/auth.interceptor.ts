import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  let clonedRequest = req;

  // Añadir token si existe
  if (token) {
    clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(clonedRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      // Manejo global de errores
      if (error.status === 401) {
        // No autorizado -> Limpiar y redirigir a login
        localStorage.removeItem('token');
        localStorage.removeItem('activeUserEmail');
        router.navigate(['/auth/login']);
      }
      
      const errorMessage = error.error?.message || 'Ocurrió un error inesperado';
      console.error(`Error ${error.status}: ${errorMessage}`);
      
      return throwError(() => new Error(errorMessage));
    })
  );
};
