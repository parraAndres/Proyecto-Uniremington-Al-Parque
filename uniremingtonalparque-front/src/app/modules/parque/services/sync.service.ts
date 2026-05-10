import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, fromEvent, merge, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { OfflineDbService } from './offline-db.service'; 
import { BackendApiService } from './backend-api.service';

@Injectable({ providedIn: 'root' })
export class SyncService {
  private isOnline$ = new BehaviorSubject<boolean>(true);
  private pendingSync$ = new BehaviorSubject<number>(0);

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private db: OfflineDbService,
    private api: BackendApiService
  ) {
    if (isPlatformBrowser(this.platformId)) {
      this.initNetworkDetection();
      this.checkPendingData();
    }
  }

  get networkStatus$(): Observable<boolean> { return this.isOnline$.asObservable(); }
  get pendingCount$(): Observable<number> { return this.pendingSync$.asObservable(); }

  private collections = ['beneficiaries', 'servicios', 'seguimientos', 'diagnosticos', 'academico', 'recursos'];

  private initNetworkDetection() {
    this.isOnline$.next(navigator.onLine);
    merge(
      fromEvent(window, 'online').pipe(map(() => true)),
      fromEvent(window, 'offline').pipe(map(() => false))
    ).subscribe(isOnline => {
      this.isOnline$.next(isOnline);
      if (isOnline) this.syncData();
    });
  }

  async saveLocally(collection: string, data: any) {
    await this.db.save(collection, data);
    await this.checkPendingData();
    if (this.isOnline$.value) {
      this.syncData();
    }
  }

  async syncData() {
    if (!this.isOnline$.value) return;

    for (const collection of this.collections) {
      const pendingItems = await this.db.getAll(collection);
      if (pendingItems.length === 0) continue;

      for (const item of pendingItems) {
        try {
          // En una PWA real aquí llamaríamos al endpoint específico según la colección.
          // Ej: await this.api.pushData(collection, item).toPromise();
          
          // Por ahora simulamos la subida exitosa
          await new Promise(resolve => setTimeout(resolve, 500));
          await this.db.delete(collection, item.id);
        } catch (error) {
          console.error(`Error sincronizando registro de ${collection}`, error);
        }
      }
    }
    await this.checkPendingData();
  }

  async checkPendingData() {
    let total = 0;
    for (const collection of this.collections) {
      const items = await this.db.getAll(collection);
      total += items.length;
    }
    this.pendingSync$.next(total);
  }
}
