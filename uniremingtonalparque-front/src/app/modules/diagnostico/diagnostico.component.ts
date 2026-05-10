import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SyncService } from '../parque/services/sync.service';

@Component({
  selector: 'app-diagnostico',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './diagnostico.component.html'
})
export class DiagnosticoComponent implements OnInit {
  form: FormGroup;
  isOnline = true;
  errorMessage = '';
  successMessage = '';

  constructor(private fb: FormBuilder, private syncService: SyncService) {
    this.form = this.fb.group({
      id: [crypto.randomUUID()],
      problematica: ['', Validators.required],
      clasificacion: ['', Validators.required],
      prioridad: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
  }

  async onSubmit() {
    if (this.form.invalid) return;
    try {
      this.errorMessage = ''; this.successMessage = '';
      await this.syncService.saveLocally('diagnosticos', this.form.value);
      this.successMessage = 'Diagnóstico guardado correctamente.';
      this.form.reset({ id: crypto.randomUUID(), problematica: '', clasificacion: '', prioridad: '' });
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al guardar';
    }
  }
}
