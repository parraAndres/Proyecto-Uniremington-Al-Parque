import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  facultades = [
    { nombre: 'Medicina Veterinaria y Zootecnia', especialidad: 'Bienestar Animal', icon: '🐾' },
    { nombre: 'Ciencias Jurídicas', especialidad: 'Brigadas Jurídicas', icon: '⚖️' },
    { nombre: 'Ciencias Empresariales', especialidad: 'Asesorías', icon: '💼' },
    { nombre: 'Diseño', especialidad: 'Imagen Corporativa', icon: '🎨' },
    { nombre: 'Salud', especialidad: 'Nutrición/Medicina', icon: '🩺' },
    { nombre: 'Contaduría', especialidad: 'Consultorio Contable', icon: '📊' },
    { nombre: 'Ingeniería', especialidad: 'Medio Ambiente', icon: '🌱' }
  ];

  modulos = [
    { nombre: 'Registro de Beneficiarios', icon: '👥', ruta: '/parque/beneficiarios' },
    { nombre: 'Registro de Servicios', icon: '📋', ruta: '/parque/servicios' },
    { nombre: 'Indicadores', icon: '📈', ruta: '/indicadores' },
    { nombre: 'Seguimiento', icon: '🔍', ruta: '/seguimiento' },
    { nombre: 'Diagnóstico Territorial', icon: '🗺️', ruta: '/diagnostico-territorial' },
    { nombre: 'Módulo Académico', icon: '🎓', ruta: '/academico' },
    { nombre: 'Recursos', icon: '📦', ruta: '/recursos' },
    { nombre: 'Modo Offline', icon: '🔌', ruta: '/offline-status' },
    { nombre: 'Panel de Control', icon: '⚙️', ruta: '/panel-control' }
  ];

  selectedFacultad: any = null;
  userRole: string = '';
  userName: string = '';

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.userRole = user.role || 'estudiante';
        this.userName = user.nombreCompleto;
        
        if (this.userRole === 'estudiante' && user.facultad) {
          // Filtrar la lista completa para dejar SOLAMENTE la facultad del usuario
          this.facultades = this.facultades.filter(f => f.nombre === user.facultad);
          if (this.facultades.length > 0) {
            this.selectedFacultad = this.facultades[0];
          } else {
            // Fallback en caso de facultad no mapeada exactamente
            this.facultades = [{ nombre: user.facultad, especialidad: 'Área Asignada', icon: '🏛️' }];
            this.selectedFacultad = this.facultades[0];
          }
        }
      }
    });
  }

  selectFacultad(f: any) {
    // Ya no es necesario que sea clicleable si solo hay una, pero lo dejamos por si acaso
    this.selectedFacultad = f;
  }
}
