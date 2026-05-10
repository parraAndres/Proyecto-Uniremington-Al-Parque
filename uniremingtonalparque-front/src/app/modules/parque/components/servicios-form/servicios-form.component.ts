import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SyncService } from '../../services/sync.service';
import { db } from '../../../../core/database/app-database';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-servicios-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './servicios-form.component.html'
})
export class ServiciosFormComponent implements OnInit {
  serviciosForm: FormGroup;
  beneficiarios: any[] = [];
  isOnline = true;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder, 
    private syncService: SyncService,
    private authService: AuthService
  ) {
    this.serviciosForm = this.fb.group({
      id: [crypto.randomUUID()],
      beneficiarioId: ['', Validators.required],
      tipoServicio: ['', Validators.required],
      facultadResponsable: [{ value: '', disabled: true }, Validators.required],
      descripcion: ['', Validators.required],
      tiempoAtencion: ['', [Validators.required, Validators.min(1)]],
      resultado: ['', Validators.required],
      observaciones: ['']
    });
  }

  async ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
    
    this.authService.currentUser$.subscribe(user => {
      if (user && user.facultad) {
        this.serviciosForm.patchValue({ facultadResponsable: user.facultad });
      }
    });

    await this.loadBeneficiaries();
  }

  async loadBeneficiaries() {
    try {
      this.beneficiarios = await db.beneficiaries.toArray();
    } catch (error) {
      console.error('Error cargando beneficiarios offline', error);
    }
  }

  async onSubmit() {
    if (this.serviciosForm.invalid) return;

    try {
      this.errorMessage = '';
      this.successMessage = '';
      // Usar getRawValue() para incluir los campos deshabilitados (Facultad Responsable)
      const data = this.serviciosForm.getRawValue();

      await this.syncService.saveLocally('servicios', data);
      
      this.successMessage = this.isOnline 
        ? 'Servicio guardado exitosamente y sincronizado.' 
        : 'Servicio guardado localmente (Offline).';
      
      const currentUserFacultad = this.serviciosForm.get('facultadResponsable')?.value;

      this.serviciosForm.reset({
        id: crypto.randomUUID(),
        beneficiarioId: '',
        tipoServicio: '',
        facultadResponsable: currentUserFacultad, // Mantener la facultad
        descripcion: '',
        tiempoAtencion: '',
        resultado: '',
        observaciones: ''
      });
      
    } catch (error: any) {
      this.errorMessage = error.message || 'Error al guardar el servicio.';
    }
  }
}
