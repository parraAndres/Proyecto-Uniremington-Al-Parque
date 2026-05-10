import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SyncService } from '../parque/services/sync.service';
import { db } from '../../core/database/app-database';

@Component({
  selector: 'app-seguimiento',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './seguimiento.component.html'
})
export class SeguimientoComponent implements OnInit {
  form: FormGroup;
  beneficiarios: any[] = [];
  isOnline = true;
  errorMessage = '';
  successMessage = '';

  constructor(private fb: FormBuilder, private syncService: SyncService) {
    this.form = this.fb.group({
      id: [crypto.randomUUID()],
      beneficiarioId: ['', Validators.required],
      estado: ['', Validators.required],
      fechaProgramada: ['', Validators.required],
      avances: ['', Validators.required]
    });
  }

  async ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
    try {
      this.beneficiarios = await db.beneficiaries.toArray();
    } catch (e) {
      console.error(e);
    }
  }

  async onSubmit() {
    if (this.form.invalid) return;
    try {
      this.errorMessage = ''; this.successMessage = '';
      await this.syncService.saveLocally('seguimientos', this.form.value);
      this.successMessage = 'Seguimiento guardado correctamente.';
      this.form.reset({ id: crypto.randomUUID(), beneficiarioId: '', estado: '', fechaProgramada: '', avances: '' });
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al guardar';
    }
  }
}
