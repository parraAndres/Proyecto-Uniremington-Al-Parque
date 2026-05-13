import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // Durante el SSR (servidor), no tenemos acceso al localStorage.
  // Para evitar que el servidor redirija erróneamente al login antes de que 
  // el navegador pueda cargar la sesión, permitimos el paso en el servidor.
  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  if (authService.isAuthenticated) {
    return true;
  }

  // Si no está autenticado en el navegador, redirigir al login
  return router.parseUrl('/login');
};
