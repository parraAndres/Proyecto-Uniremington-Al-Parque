import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SyncService } from '../parque/services/sync.service';
import { db } from '../../core/database/app-database';

@Component({
  selector: 'app-offline-status',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './offline-status.component.html',
  styles: [`
    .sync-card { background: white; padding: 1.5rem; border-radius: 8px; margin-top: 1rem; border: 1px solid #eee; }
    .status-item { display: flex; justify-content: space-between; padding: 0.75rem 0; border-bottom: 1px solid #f5f5f5; }
    .status-item:last-child { border-bottom: none; }
    .count-badge { background: var(--secondary-color, #ffcc00); color: #333; padding: 0.2rem 0.6rem; border-radius: 12px; font-weight: bold; font-size: 0.85rem; }
  `]
})
export class OfflineStatusComponent implements OnInit {
  isOnline = true;
  isSyncing = false;
  
  queues = {
    beneficiarios: 0,
    servicios: 0,
    seguimientos: 0,
    diagnosticos: 0,
    academico: 0,
    recursos: 0
  };

  get totalPending() {
    return Object.values(this.queues).reduce((a, b) => a + b, 0);
  }

  constructor(private syncService: SyncService) {}

  async ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
    this.syncService.pendingCount$.subscribe(async () => {
      await this.loadQueues();
    });
  }

  async loadQueues() {
    try {
      this.queues.beneficiarios = await db.beneficiaries.count();
      this.queues.servicios = await db.servicios.count();
      this.queues.seguimientos = await db.seguimientos.count();
      this.queues.diagnosticos = await db.diagnosticos.count();
      this.queues.academico = await db.academico.count();
      this.queues.recursos = await db.recursos.count();
    } catch (e) {
      console.error('Error loading queues', e);
    }
  }

  async forceSync() {
    if (!this.isOnline) {
      alert('No hay conexión a internet para sincronizar.');
      return;
    }
    
    this.isSyncing = true;
    try {
      // Llamar al método real de sincronización en SyncService
      await this.syncService.syncData();
      alert('Sincronización manual completada.');
    } catch (e) {
      alert('Error en la sincronización.');
    } finally {
      this.isSyncing = false;
      await this.loadQueues();
    }
  }
}
