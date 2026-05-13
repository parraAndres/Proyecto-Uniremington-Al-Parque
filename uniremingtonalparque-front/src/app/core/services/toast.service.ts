import { Injectable, signal } from '@angular/core';

export interface ToastNotification {
  id: string;
  title: string;
  message: string;
  type: 'success' | 'info' | 'warning' | 'error';
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  toasts = signal<ToastNotification[]>([]);

  show(title: string, message: string, type: 'success' | 'info' | 'warning' | 'error' = 'info') {
    const id = crypto.randomUUID();
    const newToast: ToastNotification = { id, title, message, type };
    
    this.toasts.update(currentToasts => [...currentToasts, newToast]);

    // Auto dismiss after 5 seconds
    setTimeout(() => {
      this.remove(id);
    }, 5000);
  }

  remove(id: string) {
    this.toasts.update(currentToasts => currentToasts.filter(t => t.id !== id));
  }
}
