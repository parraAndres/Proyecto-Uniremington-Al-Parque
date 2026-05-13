import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { db } from '../../../core/database/app-database';

@Component({
  selector: 'app-seguimiento-caso-beneficiario',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './seguimiento-caso-beneficiario.component.html'
})
export class SeguimientoCasoBeneficiarioComponent implements OnInit {
  seguimientos: any[] = [];
  myRecord: any = null;

  constructor(private authService: AuthService) {}

  async ngOnInit() {
    await this.loadData();
  }

  async loadData() {
    try {
      const doc = this.authService.currentUserValue?.documento;
      if (!doc) return;
      const allBenefit = await db.beneficiaries.toArray();
      this.myRecord = allBenefit.find(b => b.documento === doc);
      if (this.myRecord) {
        this.seguimientos = await db.seguimientos.where('beneficiarioId').equals(this.myRecord.id).toArray();
      }
    } catch (e) { console.error(e); }
  }

  getEstadoClass(estado: string): string {
    const map: any = { 'ABIERTO': 'open', 'EN_PROCESO': 'process', 'CERRADO': 'closed', 'VISITA_PROGRAMADA': 'visit' };
    return map[estado] || 'default';
  }

  getProximaCita(): any {
    const pendientes = this.seguimientos.filter(s => s.estado !== 'CERRADO');
    return pendientes.sort((a, b) => new Date(a.fechaProgramada).getTime() - new Date(b.fechaProgramada).getTime())[0];
  }
}
