import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SyncService } from '../parque/services/sync.service';

@Component({
  selector: 'app-recursos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './recursos.component.html'
})
export class RecursosComponent implements OnInit {
  form: FormGroup;
  isOnline = true;
  errorMessage = '';
  successMessage = '';

  constructor(private fb: FormBuilder, private syncService: SyncService) {
    this.form = this.fb.group({
      id: [crypto.randomUUID()],
      aporte: ['', Validators.required],
      tipo: ['', Validators.required],
      valorEstimado: ['']
    });
  }

  ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
  }

  async onSubmit() {
    if (this.form.invalid) return;
    try {
      this.errorMessage = ''; this.successMessage = '';
      await this.syncService.saveLocally('recursos', this.form.value);
      this.successMessage = 'Registro de recurso guardado correctamente.';
      this.form.reset({ id: crypto.randomUUID(), aporte: '', tipo: '', valorEstimado: '' });
    } catch (e: any) {
      this.errorMessage = e.message || 'Error al guardar';
    }
  }
}
