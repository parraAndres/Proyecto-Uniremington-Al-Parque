import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SyncService } from '../../services/sync.service';
import { ToastService } from '../../../../core/services/toast.service';

import { db } from '../../../../core/database/app-database';

@Component({
  selector: 'app-beneficiary-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './beneficiary-form.component.html'
})
export class BeneficiaryFormComponent implements OnInit {
  form!: FormGroup;
  isOnline = true;
  errorMessage = '';
  facultades = [
    'Medicina Veterinaria y Zootecnia', 'Ciencias Jurídicas', 
    'Ciencias Empresariales', 'Diseño', 'Salud', 'Contaduría', 'Ingeniería'
  ];

  constructor(private fb: FormBuilder, private syncService: SyncService, private toastService: ToastService) {}

  ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
    this.initForm();
  }

  initForm() {
    this.form = this.fb.group({
      id: [crypto.randomUUID()],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      documento: ['', Validators.required],
      edad: ['', [Validators.required, Validators.min(0)]],
      genero: ['', Validators.required],
      telefono: ['', Validators.required],
      municipio: ['', Validators.required],
      barrio: ['', Validators.required],
      tipoPoblacion: ['', Validators.required],
      servicioSolicitado: ['', Validators.required],
      autorizacionDatos: [false, Validators.requiredTrue]
    });
  }

  async onSubmit() {
    if (this.form.invalid) return;

    try {
      this.errorMessage = '';
      const data = this.form.value;
      
      // Validación de duplicidad por documento en Dexie
      const existing = await db.beneficiaries.where('documento').equals(data.documento).first();
      if (existing) {
        throw new Error('Ya existe un beneficiario registrado con este documento.');
      }

      if (this.isOnline) {
        console.log('Online - Enviar al backend:', data);
        await this.syncService.saveLocally('beneficiaries', data); // Guardamos local también como caché
      } else {
        await this.syncService.saveLocally('beneficiaries', data);
      }
      
      // Lanzar notificación "Efecto WOW" y simular que le llega a la persona de la facultad
      this.toastService.show(
        '¡Nuevo Beneficiario!', 
        `Se ha registrado a ${data.nombres} para ser atendido por la facultad de ${data.servicioSolicitado}. Notificando a los asesores de esa facultad...`, 
        'success'
      );
      
      this.form.reset();
      this.initForm();
      
    } catch (error: any) {
      this.errorMessage = error.message || 'Error al guardar el beneficiario.';
    }
  }
}
