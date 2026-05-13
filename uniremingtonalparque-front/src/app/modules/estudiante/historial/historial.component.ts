import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { db } from '../../../core/database/app-database';
import { SyncService } from '../../parque/services/sync.service';

@Component({
  selector: 'app-historial',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './historial.component.html'
})
export class HistorialComponent implements OnInit {
  atenciones: any[] = [];
  beneficiarios: any[] = [];
  seguimientos: any[] = [];
  isOnline = true;

  activeTab: 'servicios' | 'beneficiarios' | 'estados' = 'servicios';

  filterServicio = '';
  filterEstado = '';

  constructor(
    private authService: AuthService,
    private syncService: SyncService
  ) {}

  async ngOnInit() {
    this.syncService.networkStatus$.subscribe(s => this.isOnline = s);
    await this.loadData();
  }

  async loadData() {
    try {
      this.atenciones = await db.servicios.toArray();
      this.beneficiarios = await db.beneficiaries.toArray();
      this.seguimientos = await db.seguimientos.toArray();
    } catch (e) { console.error(e); }
  }

  getBeneficiarioNombre(id: string): string {
    const b = this.beneficiarios.find(b => b.id === id);
    return b ? `${b.nombres} ${b.apellidos}` : '—';
  }

  getSeguimientoEstado(beneficiarioId: string): string {
    const s = this.seguimientos.find(s => s.beneficiarioId === beneficiarioId);
    return s?.estado || 'Sin seguimiento';
  }

  get filteredAtenciones() {
    return this.atenciones.filter(a =>
      (!this.filterServicio || a.tipoServicio?.toLowerCase().includes(this.filterServicio.toLowerCase()))
    );
  }

  get uniqueBeneficiariosAtendidos() {
    const ids = new Set(this.atenciones.map(a => a.beneficiarioId));
    return this.beneficiarios.filter(b => ids.has(b.id));
  }

  get atencionesConEstado() {
    return this.atenciones.map(a => ({
      ...a,
      beneficiario: this.getBeneficiarioNombre(a.beneficiarioId),
      estadoCaso: this.getSeguimientoEstado(a.beneficiarioId)
    }));
  }
}
