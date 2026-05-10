import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { SyncService } from '../../../modules/parque/services/sync.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './app-shell.component.html',
  styleUrls: ['./app-shell.component.scss']
})
export class AppShellComponent implements OnInit {
  isOnline = true;
  pendingCount = 0;
  isAuthenticated = false;

  constructor(
    private syncService: SyncService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
    this.syncService.pendingCount$.subscribe(count => this.pendingCount = count);
    this.authService.currentUser$.subscribe(user => this.isAuthenticated = !!user);
  }

  get showHeaderControls(): boolean {
    return this.isAuthenticated && !this.router.url.includes('/login') && !this.router.url.includes('/register');
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
