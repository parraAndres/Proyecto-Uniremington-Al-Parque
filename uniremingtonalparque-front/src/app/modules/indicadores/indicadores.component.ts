import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { db } from '../../core/database/app-database';

@Component({
  selector: 'app-indicadores',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './indicadores.component.html',
  styles: [`
    .kpi-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-top: 1.5rem; }
    .kpi-card { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); text-align: center; border-bottom: 4px solid var(--primary-color, #004d99); }
    .kpi-number { font-size: 2.5rem; font-weight: bold; color: var(--primary-color, #004d99); margin: 0.5rem 0; }
    .kpi-title { font-size: 1rem; color: #666; margin: 0; }
  `]
})
export class IndicadoresComponent implements OnInit {
  totalBeneficiarios = 0;
  totalServicios = 0;
  totalDiagnosticos = 0;
  totalAcademico = 0;

  async ngOnInit() {
    await this.loadIndicadores();
  }

  async loadIndicadores() {
    try {
      this.totalBeneficiarios = await db.beneficiaries.count();
      this.totalServicios = await db.servicios.count();
      this.totalDiagnosticos = await db.diagnosticos.count();
      this.totalAcademico = await db.academico.count();
    } catch (e) {
      console.error('Error cargando indicadores', e);
    }
  }
}
