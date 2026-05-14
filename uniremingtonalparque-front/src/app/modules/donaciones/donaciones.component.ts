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
            <!-- Paso 1: Selección de Monto -->
            <div class="donation-card" *ngIf="step === 'amount'">
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

              <button class="btn-submit-donation" (click)="goToPayment()">
                Continuar al Pago <i class="fas fa-arrow-right"></i>
              </button>

              <div class="payment-methods-icons">
                <img src="https://logodownload.org/wp-content/uploads/2016/10/visa-logo-1.png" alt="Visa">
                <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Mastercard-logo.svg/1200px-Mastercard-logo.svg.png" alt="Mastercard">
                <img src="https://static.wixstatic.com/media/765f0e_66415777a92243e8876770669145657f~mv2.png/v1/fill/w_400,h_400,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/pse.png" alt="PSE">
              </div>
            </div>

            <!-- Paso 2: Pasarela de Pago -->
            <div class="donation-card payment-gateway" *ngIf="step === 'payment'">
              <button class="btn-back" (click)="step = 'amount'"><i class="fas fa-chevron-left"></i> Volver</button>
              <h3>Pasarela de Pagos</h3>
              <div class="summary-box">
                <span>Total a donar:</span>
                <strong>\${{ getFinalAmount() | number }}</strong>
              </div>

              <div class="payment-tabs">
                <button [class.active]="paymentMethod === 'card'" (click)="paymentMethod = 'card'">
                  <i class="fas fa-credit-card"></i> Tarjeta
                </button>
                <button [class.active]="paymentMethod === 'pse'" (click)="paymentMethod = 'pse'">
                  <i class="fas fa-university"></i> PSE
                </button>
              </div>

              <!-- Formulario Tarjeta -->
              <div class="method-content" *ngIf="paymentMethod === 'card'">
                <div class="field-group">
                  <label>Número de Tarjeta</label>
                  <input type="text" placeholder="0000 0000 0000 0000">
                </div>
                <div class="form-row">
                  <div class="field-group">
                    <label>Expiración</label>
                    <input type="text" placeholder="MM/YY">
                  </div>
                  <div class="field-group">
                    <label>CVC</label>
                    <input type="text" placeholder="123">
                  </div>
                </div>
                <div class="field-group">
                  <label>Nombre en la Tarjeta</label>
                  <input type="text" placeholder="Como aparece en la tarjeta">
                </div>
              </div>

              <!-- Formulario PSE -->
              <div class="method-content" *ngIf="paymentMethod === 'pse'">
                <div class="field-group">
                  <label>Selecciona tu Banco</label>
                  <select class="custom-select">
                    <option>Bancolombia</option>
                    <option>Banco de Bogotá</option>
                    <option>Davivienda</option>
                    <option>Nequi</option>
                    <option>Daviplata</option>
                  </select>
                </div>
                <div class="field-group">
                  <label>Tipo de Persona</label>
                  <select class="custom-select">
                    <option>Persona Natural</option>
                    <option>Persona Jurídica</option>
                  </select>
                </div>
              </div>

              <button class="btn-submit-donation" (click)="confirmPayment()" [disabled]="isProcessing">
                <span *ngIf="!isProcessing">Confirmar Donación</span>
                <span *ngIf="isProcessing" class="loader-white"></span>
              </button>
              
              <p class="secure-note"><i class="fas fa-lock"></i> Pago seguro encriptado SSL de 256 bits</p>
            </div>

            <!-- Paso 3: Éxito -->
            <div class="donation-card success-card" *ngIf="step === 'success'">
              <div class="success-icon">
                <i class="fas fa-heart"></i>
              </div>
              <h3>¡Donación Exitosa!</h3>
              <p>Tu aporte de <strong>\${{ getFinalAmount() | number }}</strong> ha sido procesado correctamente. Gracias por ayudarnos a transformar vidas.</p>
              <button class="btn-submit-donation" (click)="step = 'amount'">
                Realizar otra donación
              </button>
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
      flex: 0 0 450px;
      min-height: 500px;
    }

    .donation-card {
      background: white;
      padding: 2.5rem;
      border-radius: 20px;
      box-shadow: 0 20px 50px rgba(0,0,0,0.2);
      color: #333;
      animation: fadeIn 0.5s ease-out;

      h3 { font-size: 1.8rem; margin-bottom: 0.5rem; color: #00447b; }
      p { color: #666; margin-bottom: 1.5rem; }

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
        &:hover:not(:disabled) { transform: translateY(-3px); box-shadow: 0 10px 20px rgba(234, 10, 42, 0.3); }
        &:disabled { opacity: 0.7; cursor: not-allowed; }
      }

      .payment-methods-icons {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 1.5rem;
        margin-top: 2rem;
        img { height: 25px; filter: grayscale(1); opacity: 0.5; transition: 0.3s; &:hover { filter: none; opacity: 1; } }
      }
    }

    /* Estilos Pasarela */
    .payment-gateway {
      .btn-back { background: none; border: none; color: #ea0a2a; font-weight: 600; cursor: pointer; padding: 0; margin-bottom: 1rem; }
      .summary-box {
        background: #f8f9fa;
        padding: 1rem;
        border-radius: 10px;
        display: flex;
        justify-content: space-between;
        margin-bottom: 1.5rem;
        border: 1px dashed #ddd;
        strong { color: #ea0a2a; font-size: 1.2rem; }
      }

      .payment-tabs {
        display: flex;
        gap: 0.5rem;
        margin-bottom: 1.5rem;
        button {
          flex: 1;
          padding: 0.8rem;
          border-radius: 8px;
          border: 1px solid #eee;
          background: #fdfdfd;
          font-weight: 600;
          cursor: pointer;
          &.active { border-color: #00447b; background: #e6f0f8; color: #00447b; }
        }
      }

      .method-content {
        margin-bottom: 1.5rem;
        .field-group { margin-bottom: 1rem; }
        label { display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.4rem; color: #666; }
        input, .custom-select {
          width: 100%;
          padding: 0.8rem;
          border: 1px solid #ddd;
          border-radius: 8px;
          &:focus { border-color: #00447b; outline: none; }
        }
        .form-row { display: flex; gap: 1rem; .field-group { flex: 1; } }
      }

      .secure-note { text-align: center; font-size: 0.8rem; color: #999; margin-top: 1rem; }
    }

    /* Estilos Éxito */
    .success-card {
      text-align: center;
      .success-icon {
        width: 80px; height: 80px;
        background: #e8f5e9;
        color: #4caf50;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 2.5rem;
        margin: 0 auto 1.5rem;
        animation: heartBeat 1.5s infinite;
      }
      h3 { color: #4caf50; }
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

    .loader-white {
      width: 20px; height: 20px;
      border: 3px solid rgba(255,255,255,0.3);
      border-top-color: white;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    @keyframes spin { to { transform: rotate(360deg); } }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
    @keyframes heartBeat { 0% { transform: scale(1); } 50% { transform: scale(1.1); } 100% { transform: scale(1); } }

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
  
  step: 'amount' | 'payment' | 'success' = 'amount';
  paymentMethod: 'card' | 'pse' = 'card';
  isProcessing = false;

  selectAmount(amount: number) {
    this.selectedAmount = amount;
    this.customAmount = null;
  }

  onCustomInput() {
    this.selectedAmount = null;
  }

  getFinalAmount(): number {
    return this.selectedAmount || this.customAmount || 0;
  }

  goToPayment() {
    if (this.getFinalAmount() <= 0) {
      alert('Por favor selecciona o ingresa un monto válido.');
      return;
    }
    this.step = 'payment';
  }

  confirmPayment() {
    this.isProcessing = true;
    
    // Simulamos el tiempo de procesamiento de la pasarela
    setTimeout(() => {
      this.isProcessing = false;
      this.step = 'success';
    }, 2500);
  }
}

