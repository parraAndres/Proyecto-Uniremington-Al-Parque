import { Component, OnInit, HostListener, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { SyncService } from '../../../modules/parque/services/sync.service';
import { AuthService } from '../../services/auth.service';
import { UniNotificacionService, NotificacionUniremington } from '../../services/uni-notificacion.service';
import { interval, Subscription } from 'rxjs';
import { switchMap, startWith } from 'rxjs/operators';

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
  isAdmin = false;
  isDocente = false;
  isEstudiante = false;
  isBeneficiario = false;
  isMenuOpen = false;
  
  notifications: NotificacionUniremington[] = [];
  unreadCount = 0;
  isNotificationsOpen = false;
  private notifSubscription?: Subscription;

  @ViewChild('hamburgerMenu') hamburgerMenu?: ElementRef;

  constructor(
    private syncService: SyncService,
    private authService: AuthService,
    private notifService: UniNotificacionService,
    private router: Router
  ) {}

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
    this.isNotificationsOpen = false;
  }

  toggleNotifications() {
    this.isNotificationsOpen = !this.isNotificationsOpen;
    this.isMenuOpen = false;
    if (this.isNotificationsOpen) {
      this.loadNotifications();
    }
  }

  loadNotifications() {
    this.notifService.getNotificaciones().subscribe(notifs => {
      this.notifications = notifs;
    });
  }

  markAsRead(notif: NotificacionUniremington) {
    if (!notif.leida) {
      this.notifService.markAsRead(notif.id).subscribe(() => {
        notif.leida = true;
        this.updateUnreadCount();
      });
    }
  }

  updateUnreadCount() {
    this.notifService.getUnreadCount().subscribe(res => {
      this.unreadCount = res.unreadCount;
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const targetElement = event.target as HTMLElement;
    
    // Si el menú está abierto y hacemos clic en algo que no sea el botón ni el menú en sí, lo cerramos
    const clickedOnHamburger = targetElement.closest('.hamburger-btn');
    const clickedInsideDropdown = targetElement.closest('.nav-dropdown');
    const clickedOnNotif = targetElement.closest('.notification-bell');
    const clickedInsideNotif = targetElement.closest('.notifications-dropdown');
    
    if (this.isMenuOpen && !clickedOnHamburger && !clickedInsideDropdown) {
      this.isMenuOpen = false;
    }

    if (this.isNotificationsOpen && !clickedOnNotif && !clickedInsideNotif) {
      this.isNotificationsOpen = false;
    }
  }

  ngOnInit() {
    this.syncService.networkStatus$.subscribe(status => this.isOnline = status);
    this.syncService.pendingCount$.subscribe(count => this.pendingCount = count);
    this.authService.currentUser$.subscribe(user => {
      this.isAuthenticated = !!user;
      const rol = user?.rol?.toUpperCase();
      this.isAdmin = rol === 'ADMIN' || user?.documento === '123456';
      this.isDocente = rol === 'PROFESOR' || rol === 'DOCENTE';
      this.isEstudiante = rol === 'ESTUDIANTE' || rol === 'ESTUDIANTE_FACULTAD';
      this.isBeneficiario = rol === 'BENEFICIARIO';

      if (this.isAuthenticated && !this.isBeneficiario) {
        this.startNotificationPolling();
      } else {
        this.stopNotificationPolling();
      }
    });
  }

  private startNotificationPolling() {
    this.stopNotificationPolling();
    this.notifSubscription = interval(30000) // Polling cada 30 segundos
      .pipe(
        startWith(0),
        switchMap(() => this.notifService.getUnreadCount())
      )
      .subscribe(res => {
        this.unreadCount = res.unreadCount;
      });
  }

  private stopNotificationPolling() {
    if (this.notifSubscription) {
      this.notifSubscription.unsubscribe();
    }
  }

  get showHeaderControls(): boolean {
    return this.isAuthenticated && !this.router.url.includes('/login') && !this.router.url.includes('/register');
  }

  get showHamburger(): boolean {
    return true;
  }

  get showLayout(): boolean {
    const url = this.router.url.split('?')[0].split('#')[0];
    return !['/login', '/register'].includes(url);
  }

  get isAtPanelControl(): boolean {
    return this.router.url.includes('/panel-control');
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
