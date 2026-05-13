import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { db } from '../../../core/database/app-database';

@Component({
  selector: 'app-gestion-casos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './gestion-casos.component.html'
})
export class GestionCasosComponent implements OnInit {
  activeTab: 'crear' | 'estados' | 'visitas' = 'crear';

  seguimientoForm: FormGroup;
  visitaForm: FormGroup;
  estadoForm: FormGroup;

  seguimientos: any[] = [];
  beneficiarios: any[] = [];
  successMessage = '';
  errorMessage = '';

  estadosDisponibles = ['ABIERTO', 'EN_PROCESO', 'CERRADO'];

  constructor(private fb: FormBuilder) {
    this.seguimientoForm = this.fb.group({
      id: [crypto.randomUUID()],
      beneficiarioId: ['', Validators.required],
      estado: ['ABIERTO', Validators.required],
      fechaProgramada: ['', Validators.required],
      avances: ['', Validators.required],
      observaciones: ['']
    });

    this.visitaForm = this.fb.group({
      seguimientoId: ['', Validators.required],
      fechaVisita: ['', Validators.required],
      lugar: ['', Validators.required],
      objetivo: ['', Validators.required],
      responsable: ['', Validators.required]
    });

    this.estadoForm = this.fb.group({
      seguimientoId: ['', Validators.required],
      nuevoEstado: ['', Validators.required],
      motivoCambio: ['', Validators.required]
    });
  }

  async ngOnInit() {
    await this.loadData();
  }

  async loadData() {
    try {
      this.seguimientos = await db.seguimientos.toArray();
      this.beneficiarios = await db.beneficiaries.toArray();
    } catch (e) { console.error(e); }
  }

  getBeneficiarioNombre(id: string): string {
    const b = this.beneficiarios.find(b => b.id === id);
    return b ? `${b.nombres} ${b.apellidos}` : id;
  }

  async crearSeguimiento() {
    if (this.seguimientoForm.invalid) return;
    try {
      this.clearMessages();
      await db.seguimientos.put(this.seguimientoForm.value);
      this.successMessage = 'Seguimiento creado correctamente.';
      this.seguimientoForm.reset({ id: crypto.randomUUID(), estado: 'ABIERTO' });
      await this.loadData();
      setTimeout(() => this.successMessage = '', 3000);
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al crear seguimiento';
    }
  }

  async cambiarEstado() {
    if (this.estadoForm.invalid) return;
    try {
      this.clearMessages();
      const { seguimientoId, nuevoEstado, motivoCambio } = this.estadoForm.value;
      const seg = this.seguimientos.find(s => s.id === seguimientoId);
      if (!seg) { this.errorMessage = 'Seguimiento no encontrado'; return; }
      const updated = { ...seg, estado: nuevoEstado, motivoCambio, fechaCambioEstado: new Date().toISOString() };
      await db.seguimientos.put(updated);
      this.successMessage = `Estado cambiado a ${nuevoEstado} correctamente.`;
      this.estadoForm.reset();
      await this.loadData();
      setTimeout(() => this.successMessage = '', 3000);
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al cambiar estado';
    }
  }

  async programarVisita() {
    if (this.visitaForm.invalid) return;
    try {
      this.clearMessages();
      const visita = { id: crypto.randomUUID(), ...this.visitaForm.value, tipo: 'visita' };
      // Guardar en colección de seguimientos como tipo visita
      await db.seguimientos.put({
        id: visita.id,
        beneficiarioId: visita.seguimientoId,
        estado: 'VISITA_PROGRAMADA',
        fechaProgramada: visita.fechaVisita,
        avances: `Visita: ${visita.objetivo} en ${visita.lugar} - Responsable: ${visita.responsable}`
      });
      this.successMessage = 'Visita programada correctamente.';
      this.visitaForm.reset();
      await this.loadData();
      setTimeout(() => this.successMessage = '', 3000);
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al programar visita';
    }
  }

  clearMessages() {
    this.successMessage = '';
    this.errorMessage = '';
  }

  getEstadoBadgeClass(estado: string): string {
    const map: any = { 'ABIERTO': 'badge-open', 'EN_PROCESO': 'badge-process', 'CERRADO': 'badge-closed', 'VISITA_PROGRAMADA': 'badge-visit' };
    return map[estado] || 'badge-default';
  }
}
