import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { db } from '../../../core/database/app-database';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-beneficiario-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './beneficiario-dashboard.component.html'
})
export class BeneficiarioDashboardComponent implements OnInit {
  user: any = null;
  atenciones: any[] = [];
  seguimientos: any[] = [];

  modulos = [
    {
      nombre: 'Mis Atenciones Recibidas',
      descripcion: 'Servicios recibidos, fechas y facultades',
      icon: 'fas fa-hand-holding-heart',
      ruta: '/beneficiario/mis-atenciones',
      color: 'blue'
    },
    {
      nombre: 'Seguimiento de Caso',
      descripcion: 'Ver avances, recomendaciones y próximas citas',
      icon: 'fas fa-search-plus',
      ruta: '/beneficiario/seguimiento-caso',
      color: 'teal'
    },
    {
      nombre: 'Mi Perfil',
      descripcion: 'Actualizar datos personales y de contacto',
      icon: 'fas fa-user-edit',
      ruta: '/beneficiario/perfil',
      color: 'purple'
    }
  ];

  constructor(private authService: AuthService) {}

  async ngOnInit() {
    this.authService.currentUser$.subscribe(u => this.user = u);
    await this.loadData();
  }

  async loadData() {
    try {
      const userDoc = this.authService.currentUserValue?.documento;
      // Load all and filter by user document
      const allBenefit = await db.beneficiaries.toArray();
      const myRecord = allBenefit.find(b => b.documento === userDoc);
      if (myRecord) {
        this.atenciones = await db.servicios.where('beneficiarioId').equals(myRecord.id).toArray();
        this.seguimientos = await db.seguimientos.where('beneficiarioId').equals(myRecord.id).toArray();
      } else {
        this.atenciones = [];
        this.seguimientos = [];
      }
    } catch (e) { console.error(e); }
  }
}
