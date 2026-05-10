# Diseño de Arquitectura Frontend: Uniremington al Parque

Este documento detalla la arquitectura propuesta para el frontend de la aplicación "Uniremington al Parque", utilizando Angular v17+ con un enfoque Offline-First, renderizado de alto rendimiento (SSR/SSG) y arquitectura de estilos escalable.

## User Review Required

> [!IMPORTANT]
> Se requiere revisión de la estructura SCSS 7-1 y las estrategias de pre-renderizado (SSG) de las rutas seleccionadas. Además, confirmar si el esquema propuesto para la sincronización con el backend a través de IndexedDB cumple con las expectativas del negocio.

## Proposed Changes

### 1. Configuración de `angular.json` (SSR y SSG)

Para Angular 17+, al usar el nuevo Application Builder (`@angular-devkit/build-angular:application`), la configuración de SSR y SSG (Prerendering) se integra directamente en las opciones de compilación.

#### [MODIFY] `angular.json`
Modificaremos la sección `architect.build.options` para habilitar SSR e indicar el archivo de rutas a pre-renderizar:

```json
"architect": {
  "build": {
    "builder": "@angular-devkit/build-angular:application",
    "options": {
      "outputPath": "dist/veterinaria-front",
      "index": "src/index.html",
      "browser": "src/main.ts",
      "server": "src/main.server.ts",
      "prerender": {
        "discoverRoutes": true,
        "routesFile": "routes.txt"
      },
      "ssr": {
        "entry": "server.ts"
      },
      "polyfills": ["zone.js"],
      "tsConfig": "tsconfig.app.json",
      "inlineStyleLanguage": "scss",
      // ... resto de opciones
    }
  }
}
```
*Se requerirá crear un archivo `routes.txt` en la raíz con las rutas estáticas (ej: `/`, `/diagnostico-territorial`, `/panel-control`).*

---

### 2. Arquitectura SCSS (Patrón 7-1)

Se implementará una arquitectura escalable para los estilos institucionales, centralizando colores, tipografías y componentes en la carpeta `src/styles/`.

#### [NEW] Estructura en `src/styles/`
```text
src/styles/
├── abstracts/
│   ├── _variables.scss     # Colores Uniremington, breakpoints, etc.
│   ├── _mixins.scss        # Mixins de flexbox, media queries
│   └── _functions.scss     # Funciones útiles (px to rem)
├── base/
│   ├── _reset.scss         # Reseteo de estilos globales
│   └── _typography.scss    # Fuentes y jerarquía de textos
├── components/
│   ├── _forms.scss         # Estilos globales de formularios
│   └── _buttons.scss       # Botones institucionales
├── layout/
│   ├── _shell.scss         # Shell de la aplicación (Header, Footer, Nav)
│   └── _grid.scss          # Sistema de grillas custom
├── pages/
│   ├── _home.scss          # Estilos específicos de la portada
│   └── _dashboard.scss     # Estilos del Panel de Control
├── themes/
│   └── _uniremington.scss  # Tema principal (Modo claro/oscuro)
├── vendors/
│   └── _angular-material.scss # Ajustes sobre componentes de terceros
└── styles.scss             # Punto de entrada que importa todos los módulos
```

---

### 3. Servicio de Sincronización (Offline-First & Network Detection)

Un servicio Angular basado en RxJS para detectar conectividad y orquestar el guardado/subida de datos.

#### [NEW] `src/app/modules/parque/services/sync.service.ts`
```typescript
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, fromEvent, merge, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
// Asume existencia de un servicio IndexedDB y uno HTTP
import { OfflineDbService } from './offline-db.service'; 
import { BackendApiService } from './backend-api.service';

@Injectable({ providedIn: 'root' })
export class SyncService {
  private isOnline$ = new BehaviorSubject<boolean>(true);
  private pendingSync$ = new BehaviorSubject<number>(0);

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private db: OfflineDbService,
    private api: BackendApiService
  ) {
    if (isPlatformBrowser(this.platformId)) {
      this.initNetworkDetection();
      this.checkPendingData();
    }
  }

  get networkStatus$(): Observable<boolean> { return this.isOnline$.asObservable(); }
  get pendingCount$(): Observable<number> { return this.pendingSync$.asObservable(); }

  private initNetworkDetection() {
    this.isOnline$.next(navigator.onLine);
    merge(
      fromEvent(window, 'online').pipe(map(() => true)),
      fromEvent(window, 'offline').pipe(map(() => false))
    ).subscribe(isOnline => {
      this.isOnline$.next(isOnline);
      if (isOnline) this.syncData();
    });
  }

  async saveLocally(collection: string, data: any) {
    await this.db.save(collection, data);
    this.checkPendingData();
  }

  private async syncData() {
    const pendingItems = await this.db.getAll('beneficiaries');
    if (pendingItems.length === 0) return;

    for (const item of pendingItems) {
      try {
        await this.api.pushBeneficiary(item).toPromise();
        await this.db.delete('beneficiaries', item.id);
      } catch (error) {
        // Manejar error de duplicidad (ej. HTTP 409) u otros
        console.error('Error sincronizando registro', error);
      }
    }
    this.checkPendingData();
  }

  private async checkPendingData() {
    const items = await this.db.getAll('beneficiaries');
    this.pendingSync$.next(items.length);
  }
}
```

---

### 4. Módulo de Registro y Non-destructive Hydration

Para asegurar la hidratación, `provideClientHydration()` debe estar en `app.config.ts`. El componente de registro usará formularios reactivos, consultará el estado de red y guardará temporalmente en IndexedDB.

#### [NEW] `src/app/modules/parque/components/beneficiary-form/beneficiary-form.component.ts`
```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SyncService } from '../../services/sync.service';

@Component({
  selector: 'app-beneficiary-form',
  templateUrl: './beneficiary-form.component.html',
  styleUrls: ['./beneficiary-form.component.scss'],
  // NgOptimizedImage y otros imports omitidos por brevedad
})
export class BeneficiaryFormComponent implements OnInit {
  form!: FormGroup;
  isOnline = true;

  constructor(private fb: FormBuilder, private syncService: SyncService) {}

  ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
    this.initForm();
  }

  initForm() {
    this.form = this.fb.group({
      id: [crypto.randomUUID()], // ID temporal para IndexedDB
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      municipio: ['', Validators.required],
      // Otros campos...
    });
  }

  async onSubmit() {
    if (this.form.invalid) return;

    const data = this.form.value;
    if (this.isOnline) {
      // Intentar enviar directo, si falla cae en catch y va a local
      // this.api.pushBeneficiary(data).subscribe(...)
    } else {
      await this.syncService.saveLocally('beneficiaries', data);
      this.form.reset();
      this.initForm();
      alert('Guardado localmente. Se sincronizará al recuperar conexión.');
    }
  }
}
```

#### [MODIFY] `src/app/app.config.ts`
Asegurar que la hidratación esté habilitada:
```typescript
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideClientHydration } from '@angular/platform-browser';
// ...

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideClientHydration() // Habilita Non-destructive Hydration en Angular 17+
  ]
};
```

## Verification Plan

### Test Manuales
1. Apagar red en DevTools (Offline). Llenar formulario de beneficiario y enviarlo.
2. Verificar que los datos se guarden en IndexedDB (Application tab en DevTools).
3. Restablecer red (Online). Verificar que se haga la petición POST al backend y la IndexedDB se limpie.
4. Inspeccionar el código fuente (View Page Source) de las rutas `/` y `/diagnostico-territorial` y confirmar que contienen el HTML pre-renderizado.
5. Comprobar que los estilos aplican correctamente respetando las variables SCSS de la arquitectura 7-1.
