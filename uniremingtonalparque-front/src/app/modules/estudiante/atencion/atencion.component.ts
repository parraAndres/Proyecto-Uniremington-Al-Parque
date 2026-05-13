import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SyncService } from '../../parque/services/sync.service';
import { ToastService } from '../../../core/services/toast.service';
import { db } from '../../../core/database/app-database';

@Component({
  selector: 'app-atencion',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './atencion.component.html'
})
export class AtencionComponent implements OnInit {
  form: FormGroup;
  beneficiarios: any[] = [];
  filteredBeneficiarios: any[] = [];
  searchTerm = '';
  isOnline = true;
  successMessage = '';
  errorMessage = '';
  startTime?: Date;
  elapsedMinutes = 0;
  timerInterval: any;
  isTimerRunning = false;

  servicios = [
    'Asesoría Jurídica', 'Consulta Veterinaria', 'Asesoría Contable',
    'Consulta Nutricional', 'Diseño Gráfico', 'Asesoría Empresarial',
    'Atención en Salud', 'Educación Ambiental', 'Otro'
  ];

  constructor(
    private fb: FormBuilder,
    private syncService: SyncService,
    private toastService: ToastService
  ) {
    this.form = this.fb.group({
      id: [crypto.randomUUID()],
      beneficiarioId: ['', Validators.required],
      tipoServicio: ['', Validators.required],
      facultadResponsable: [''],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      observaciones: [''],
      tiempoAtencion: ['', [Validators.required, Validators.min(1)]],
      resultado: ['orientacion', Validators.required],
      fechaAtencion: [new Date().toISOString().split('T')[0]]
    });
  }

  async ngOnInit() {
    this.syncService.networkStatus$.subscribe(s => this.isOnline = s);
    await this.loadBeneficiarios();
  }

  async loadBeneficiarios() {
    try {
      this.beneficiarios = await db.beneficiaries.toArray();
      this.filteredBeneficiarios = [...this.beneficiarios];
    } catch (e) {}
  }

  searchBeneficiario(term: string) {
    this.searchTerm = term;
    this.filteredBeneficiarios = this.beneficiarios.filter(b =>
      b.nombres?.toLowerCase().includes(term.toLowerCase()) ||
      b.apellidos?.toLowerCase().includes(term.toLowerCase()) ||
      b.documento?.includes(term)
    );
  }

  selectBeneficiario(b: any) {
    this.form.patchValue({ beneficiarioId: b.id });
    this.searchTerm = `${b.nombres} ${b.apellidos} (${b.documento})`;
    this.filteredBeneficiarios = [];
  }

  startTimer() {
    this.startTime = new Date();
    this.isTimerRunning = true;
    this.timerInterval = setInterval(() => {
      const now = new Date();
      this.elapsedMinutes = Math.floor((now.getTime() - this.startTime!.getTime()) / 60000);
      this.form.patchValue({ tiempoAtencion: this.elapsedMinutes || 1 });
    }, 10000);
  }

  stopTimer() {
    clearInterval(this.timerInterval);
    this.isTimerRunning = false;
    this.form.patchValue({ tiempoAtencion: this.elapsedMinutes || 1 });
  }

  async onSubmit() {
    if (this.form.invalid) return;
    try {
      this.clearMessages();
      if (this.isTimerRunning) this.stopTimer();
      await this.syncService.saveLocally('servicios', this.form.value);
      this.toastService.show('¡Atención Registrada!', 'El servicio fue guardado correctamente.', 'success');
      this.successMessage = this.isOnline ? 'Atención registrada y sincronizada.' : 'Atención guardada localmente (Offline).';
      this.form.reset({ id: crypto.randomUUID(), resultado: 'orientacion', fechaAtencion: new Date().toISOString().split('T')[0] });
      this.searchTerm = '';
      this.elapsedMinutes = 0;
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al guardar la atención.';
    }
  }

  clearMessages() { this.successMessage = ''; this.errorMessage = ''; }
}
