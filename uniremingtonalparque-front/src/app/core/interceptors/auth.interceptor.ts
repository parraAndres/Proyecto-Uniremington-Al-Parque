import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const isBrowser = isPlatformBrowser(platformId);

  let token = null;
  if (isBrowser) {
    token = localStorage.getItem('token');
  }

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
      if (error.status === 401 && isBrowser) {
        // No autorizado -> Limpiar y redirigir a login
        localStorage.removeItem('token');
        localStorage.removeItem('activeUserEmail');
        router.navigate(['/auth/login']);
      }
      
      const errorMessage = error.error?.message || 'Ocurrió un error inesperado';
      if (isBrowser) {
        console.error(`Error ${error.status}: ${errorMessage}`);
      }
      
      return throwError(() => new Error(errorMessage));
    })
  );
};
