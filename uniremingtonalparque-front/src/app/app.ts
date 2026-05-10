import { Component, signal } from '@angular/core';
import { AppShellComponent } from './core/components/app-shell/app-shell.component';

@Component({
  selector: 'app-root',
  imports: [AppShellComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('uniremingtonalparque-front');
}
