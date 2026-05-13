import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { db } from '../../../core/database/app-database';

@Component({
  selector: 'app-supervision',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterModule],
  templateUrl: './supervision.component.html'
})
export class SupervisionComponent implements OnInit {
  activeTab: 'atenciones' | 'validar' | 'observaciones' | 'seguimientos' = 'atenciones';

  atenciones: any[] = [];
  seguimientos: any[] = [];
  beneficiarios: any[] = [];

  editingId: string | null = null;
  editForm: FormGroup;
  approveForm: FormGroup;

  successMessage = '';
  errorMessage = '';

  constructor(private fb: FormBuilder) {
    this.editForm = this.fb.group({
      observaciones: ['', Validators.required]
    });
    this.approveForm = this.fb.group({
      comentarioAprobacion: ['']
    });
  }

  async ngOnInit() {
    await this.loadAll();
  }

  async loadAll() {
    try {
      this.atenciones = await db.servicios.toArray();
      this.seguimientos = await db.seguimientos.toArray();
      this.beneficiarios = await db.beneficiaries.toArray();
    } catch (e) {
      console.error('Error cargando datos', e);
    }
  }

  getBeneficiarioNombre(id: string): string {
    const b = this.beneficiarios.find(b => b.id === id);
    return b ? `${b.nombres} ${b.apellidos}` : id;
  }

  startEditObservacion(atencion: any) {
    this.editingId = atencion.id;
    this.editForm.patchValue({ observaciones: atencion.observaciones || '' });
  }

  async saveObservacion(atencion: any) {
    try {
      const updated = { ...atencion, observaciones: this.editForm.value.observaciones, validado: false };
      await db.servicios.put(updated);
      this.editingId = null;
      this.successMessage = 'Observación actualizada correctamente.';
      await this.loadAll();
      setTimeout(() => this.successMessage = '', 3000);
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al guardar';
    }
  }

  async validarRegistro(atencion: any) {
    try {
      const updated = { ...atencion, validado: true, fechaValidacion: new Date().toISOString() };
      await db.servicios.put(updated);
      this.successMessage = 'Registro validado correctamente.';
      await this.loadAll();
      setTimeout(() => this.successMessage = '', 3000);
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al validar';
    }
  }

  async aprobarSeguimiento(seg: any) {
    try {
      const updated = { ...seg, aprobado: true, fechaAprobacion: new Date().toISOString() };
      await db.seguimientos.put(updated);
      this.successMessage = 'Seguimiento aprobado.';
      await this.loadAll();
      setTimeout(() => this.successMessage = '', 3000);
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al aprobar';
    }
  }

  cancelEdit() {
    this.editingId = null;
    this.editForm.reset();
  }
}
