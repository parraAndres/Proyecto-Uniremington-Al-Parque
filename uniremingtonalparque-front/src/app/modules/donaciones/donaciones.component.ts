import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-donaciones',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="donations-page">
      <section class="donations-hero">
        <div class="container hero-content">
          <div class="hero-text">
            <h1>Transforma Vidas con tu Generosidad</h1>
            <p>Tu aporte permite que "Uniremington al Parque" llegue a más municipios y beneficie a miles de personas con servicios gratuitos de calidad.</p>
            <div class="impact-badges">
              <div class="badge"><i class="fas fa-user-check"></i> +5,000 Atendidos</div>
              <div class="badge"><i class="fas fa-map-marked-alt"></i> 25 Municipios</div>
            </div>
          </div>
          <div class="donation-card-container">
            <div class="donation-card">
              <h3>Realizar una Donación</h3>
              <p>Selecciona o ingresa el monto que deseas aportar.</p>
              
              <div class="amount-grid">
                <button *ngFor="let amount of amounts" 
                        (click)="selectAmount(amount)"
                        [class.selected]="selectedAmount === amount">
                  \${{ amount | number }}
                </button>
              </div>

              <div class="custom-amount">
                <span class="currency">$</span>
                <input type="number" placeholder="Otro monto" [(ngModel)]="customAmount" (input)="onCustomInput()">
              </div>

              <button class="btn-submit-donation" (click)="processDonation()">
                Donar Ahora <i class="fas fa-heart"></i>
              </button>

              <div class="payment-methods">
                <i class="fab fa-cc-visa"></i>
                <i class="fab fa-cc-mastercard"></i>
                <i class="fab fa-cc-paypal"></i>
                <i class="fas fa-university"></i>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="container transparency-section">
        <div class="section-header">
          <h2>Transparencia y Confianza</h2>
          <p>¿A dónde va tu dinero? Garantizamos que cada peso sea invertido en el bienestar social.</p>
        </div>
        <div class="transparency-grid">
          <div class="t-item">
            <i class="fas fa-medkit"></i>
            <h4>Insumos Médicos</h4>
            <p>Materiales para jornadas de salud y atención veterinaria.</p>
          </div>
          <div class="t-item">
            <i class="fas fa-bus"></i>
            <h4>Logística de Transporte</h4>
            <p>Traslado de estudiantes y especialistas a zonas rurales.</p>
          </div>
          <div class="t-item">
            <i class="fas fa-graduation-cap"></i>
            <h4>Material Educativo</h4>
            <p>Talleres y guías para el desarrollo comunitario.</p>
          </div>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .donations-page {
      min-height: 100vh;
      background: #fdfdfd;
    }
    .donations-hero {
      background: linear-gradient(135deg, #00447b 0%, #002d52 100%);
      padding: 6rem 0;
      color: white;
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: -50%; right: -20%;
        width: 800px; height: 800px;
        background: rgba(234, 10, 42, 0.1);
        border-radius: 50%;
        z-index: 0;
      }
    }

    .hero-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 4rem;
      position: relative;
      z-index: 1;
    }

    .hero-text {
      flex: 1;
      h1 { font-size: 3.5rem; font-weight: 800; margin-bottom: 1.5rem; line-height: 1.1; }
      p { font-size: 1.25rem; opacity: 0.9; margin-bottom: 2rem; line-height: 1.6; }
      
      .impact-badges {
        display: flex;
        gap: 1rem;
        .badge {
          background: rgba(255,255,255,0.1);
          padding: 0.8rem 1.5rem;
          border-radius: 50px;
          border: 1px solid rgba(255,255,255,0.2);
          display: flex;
          align-items: center;
          gap: 0.5rem;
          font-weight: 600;
        }
      }
    }

    .donation-card-container {
      flex: 0 0 400px;
    }

    .donation-card {
      background: white;
      padding: 2.5rem;
      border-radius: 20px;
      box-shadow: 0 20px 50px rgba(0,0,0,0.2);
      color: #333;

      h3 { font-size: 1.8rem; margin-bottom: 0.5rem; color: #00447b; }
      p { color: #666; margin-bottom: 2rem; }

      .amount-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 0.8rem;
        margin-bottom: 1.5rem;

        button {
          padding: 1rem;
          border: 2px solid #eee;
          border-radius: 12px;
          background: none;
          font-weight: 700;
          cursor: pointer;
          transition: all 0.3s;
          &:hover { border-color: #ea0a2a; color: #ea0a2a; }
          &.selected { background: #ea0a2a; border-color: #ea0a2a; color: white; }
        }
      }

      .custom-amount {
        position: relative;
        margin-bottom: 2rem;
        .currency { position: absolute; left: 1.2rem; top: 50%; transform: translateY(-50%); font-weight: 700; color: #00447b; }
        input {
          width: 100%;
          padding: 1rem 1rem 1rem 2.5rem;
          border: 2px solid #eee;
          border-radius: 12px;
          font-size: 1.1rem;
          font-weight: 600;
          &:focus { border-color: #00447b; outline: none; }
        }
      }

      .btn-submit-donation {
        width: 100%;
        padding: 1.2rem;
        background: #ea0a2a;
        color: white;
        border: none;
        border-radius: 12px;
        font-size: 1.2rem;
        font-weight: 800;
        cursor: pointer;
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 0.8rem;
        transition: transform 0.3s, box-shadow 0.3s;
        &:hover { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(234, 10, 42, 0.3); }
      }

      .payment-methods {
        display: flex;
        justify-content: center;
        gap: 1.5rem;
        margin-top: 2rem;
        font-size: 1.8rem;
        color: #ddd;
      }
    }

    .transparency-section {
      padding: 6rem 0;
      .section-header { text-align: center; margin-bottom: 4rem; }
      .transparency-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 3rem;

        .t-item {
          text-align: center;
          i { font-size: 3rem; color: #ea0a2a; margin-bottom: 1.5rem; }
          h4 { font-size: 1.4rem; margin-bottom: 1rem; color: #00447b; }
          p { color: #666; line-height: 1.6; }
        }
      }
    }

    @media (max-width: 992px) {
      .hero-content { flex-direction: column; text-align: center; }
      .hero-text h1 { font-size: 2.5rem; }
      .impact-badges { justify-content: center; }
      .donation-card-container { flex: 1; width: 100%; max-width: 450px; }
      .transparency-grid { grid-template-columns: 1fr; }
    }
  `]
})
export class DonacionesComponent {
  amounts = [20000, 50000, 100000, 200000, 500000, 1000000];
  selectedAmount: number | null = 50000;
  customAmount: number | null = null;

  selectAmount(amount: number) {
    this.selectedAmount = amount;
    this.customAmount = null;
  }

  onCustomInput() {
    this.selectedAmount = null;
  }

  processDonation() {
    const finalAmount = this.selectedAmount || this.customAmount;
    if (!finalAmount || finalAmount <= 0) {
      alert('Por favor selecciona o ingresa un monto válido.');
      return;
    }
    
    alert(`¡Gracias por tu intención de donar $${finalAmount.toLocaleString()}! Redirigiendo a la pasarela de pagos segura...`);
    // Aquí iría la integración con PayU, Wompi, etc.
  }
}
