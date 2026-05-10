import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SyncService } from '../parque/services/sync.service';

@Component({
  selector: 'app-academico',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './academico.component.html'
})
export class AcademicoComponent implements OnInit {
  form: FormGroup;
  isOnline = true;
  errorMessage = '';
  successMessage = '';

  constructor(private fb: FormBuilder, private syncService: SyncService) {
    this.form = this.fb.group({
      id: [crypto.randomUUID()],
      estudiante: ['', Validators.required],
      programa: ['', Validators.required],
      horas: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
  }

  async onSubmit() {
    if (this.form.invalid) return;
    try {
      this.errorMessage = ''; this.successMessage = '';
      await this.syncService.saveLocally('academico', this.form.value);
      this.successMessage = 'Registro académico guardado correctamente.';
      this.form.reset({ id: crypto.randomUUID(), estudiante: '', programa: '', horas: '' });
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al guardar';
    }
  }
}
