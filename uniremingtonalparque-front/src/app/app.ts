import { Component, signal } from '@angular/core';
import { AppShellComponent } from './core/components/app-shell/app-shell.component';
import { AppToastComponent } from './core/components/app-toast/app-toast.component';

@Component({
  selector: 'app-root',
  imports: [AppShellComponent, AppToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('uniremingtonalparque-front');
}
