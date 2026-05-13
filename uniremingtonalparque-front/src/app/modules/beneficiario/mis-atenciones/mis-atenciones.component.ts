import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { db } from '../../../core/database/app-database';

@Component({
  selector: 'app-mis-atenciones',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './mis-atenciones.component.html'
})
export class MisAtencionesComponent implements OnInit {
  atenciones: any[] = [];
  myRecord: any = null;

  constructor(private authService: AuthService) {}

  async ngOnInit() {
    await this.loadData();
  }

  async loadData() {
    try {
      const doc = this.authService.currentUserValue?.documento;
      if (!doc) return;
      const allBenefit = await db.beneficiaries.toArray();
      this.myRecord = allBenefit.find(b => b.documento === doc);
      if (this.myRecord) {
        this.atenciones = await db.servicios.where('beneficiarioId').equals(this.myRecord.id).toArray();
      }
    } catch (e) { console.error(e); }
  }

  getFacultadLabel(fac: string): string {
    return fac || 'Facultad UAP';
  }
}
