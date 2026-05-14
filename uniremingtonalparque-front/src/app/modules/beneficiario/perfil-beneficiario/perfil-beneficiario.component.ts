import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { db } from '../../../core/database/app-database';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-perfil-beneficiario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './perfil-beneficiario.component.html'
})
export class PerfilBeneficiarioComponent implements OnInit {
  form: FormGroup;
  user: any = null;
  myRecord: any = null;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService
  ) {
    this.form = this.fb.group({
      telefono: ['', Validators.required],
      direccion: ['', Validators.required],
      barrio: ['', Validators.required],
      municipio: ['', Validators.required],
      tipoPoblacion: [''],
      // Datos básicos (read-only, shown but not editable)
    });
  }

  async ngOnInit() {
    this.authService.currentUser$.subscribe(u => this.user = u);
    await this.loadProfile();
  }

  async loadProfile() {
    try {
      const doc = this.authService.currentUserValue?.documento;
      if (!doc) return;
      const allBenefit = await db.beneficiaries.toArray();
      this.myRecord = allBenefit.find(b => b.documento === doc);
      if (this.myRecord) {
        this.form.patchValue({
          telefono: this.myRecord.telefono,
          direccion: this.myRecord.direccion,
          barrio: this.myRecord.barrio,
          municipio: this.myRecord.municipio,
          tipoPoblacion: this.myRecord.tipoPoblacion
        });
      }
    } catch (e) { console.error(e); }
  }

  async onSave() {
    if (this.form.invalid || !this.myRecord) return;
    try {
      this.clearMessages();
      const updated = { ...this.myRecord, ...this.form.value };
      await db.beneficiaries.put(updated);
      this.myRecord = updated;
      this.toastService.show('Perfil Actualizado', 'Tus datos han sido actualizados correctamente.', 'success');
      this.successMessage = 'Datos actualizados correctamente.';
      setTimeout(() => this.successMessage = '', 3000);
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al actualizar datos.';
    }
  }

  clearMessages() { this.successMessage = ''; this.errorMessage = ''; }
}
