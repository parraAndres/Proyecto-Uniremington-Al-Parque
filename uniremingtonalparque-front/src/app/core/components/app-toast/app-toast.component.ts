import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container">
      <div *ngFor="let toast of toastService.toasts()" 
           class="toast-card toast-{{toast.type}} animate-fade-in"
           (click)="toastService.remove(toast.id)">
        <div class="toast-icon">
          <i class="fas" [ngClass]="{
            'fa-check-circle': toast.type === 'success',
            'fa-info-circle': toast.type === 'info',
            'fa-exclamation-triangle': toast.type === 'warning',
            'fa-times-circle': toast.type === 'error'
          }"></i>
        </div>
        <div class="toast-content">
          <h4>{{ toast.title }}</h4>
          <p>{{ toast.message }}</p>
        </div>
        <button class="toast-close" (click)="toastService.remove(toast.id); $event.stopPropagation()">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 12px;
      pointer-events: none;
    }
    .toast-card {
      width: 350px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.15);
      display: flex;
      align-items: center;
      padding: 16px;
      pointer-events: auto;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      border-left: 6px solid #ccc;
      transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    }
    .toast-card:hover {
      transform: translateX(-5px);
    }
    .toast-success { border-left-color: #28a745; }
    .toast-success .toast-icon i { color: #28a745; }
    
    .toast-info { border-left-color: #00acc1; }
    .toast-info .toast-icon i { color: #00acc1; }
    
    .toast-warning { border-left-color: #ffc107; }
    .toast-warning .toast-icon i { color: #ffc107; }
    
    .toast-error { border-left-color: #dc3545; }
    .toast-error .toast-icon i { color: #dc3545; }

    .toast-icon {
      font-size: 24px;
      margin-right: 16px;
    }
    .toast-content {
      flex: 1;
    }
    .toast-content h4 {
      margin: 0 0 4px 0;
      color: #333;
      font-size: 15px;
      font-weight: 600;
    }
    .toast-content p {
      margin: 0;
      color: #666;
      font-size: 13px;
      line-height: 1.4;
    }
    .toast-close {
      background: none;
      border: none;
      color: #999;
      cursor: pointer;
      font-size: 16px;
      padding: 4px;
      transition: color 0.2s;
    }
    .toast-close:hover {
      color: #333;
    }
    .animate-fade-in {
      animation: slideInRight 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
    }
    @keyframes slideInRight {
      from { opacity: 0; transform: translateX(100%); }
      to { opacity: 1; transform: translateX(0); }
    }
  `]
})
export class AppToastComponent {
  toastService = inject(ToastService);
}
