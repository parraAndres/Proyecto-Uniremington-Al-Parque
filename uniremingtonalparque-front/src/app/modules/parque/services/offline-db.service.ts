import { Injectable } from '@angular/core';
import { db } from '../../../core/database/app-database';

@Injectable({ providedIn: 'root' })
export class OfflineDbService {
  async save(collection: string, data: any): Promise<void> {
    await db.table(collection).put(data);
  }
  
  async getAll(collection: string): Promise<any[]> {
    return await db.table(collection).toArray();
  }
  
  async delete(collection: string, id: string): Promise<void> {
    await db.table(collection).delete(id);
  }
}
