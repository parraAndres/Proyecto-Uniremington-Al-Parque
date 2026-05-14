import { Component, OnInit, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { StatsService, ImpactStats } from '../../core/services/stats.service';
import { NewsService, Noticia } from '../../core/services/news.service';
import { JornadaService, Jornada } from '../../core/services/jornada.service';
import { interval, Subscription } from 'rxjs';
import { startWith, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss']
})
export class LandingComponent implements OnInit, OnDestroy {
  currentSlide = 0;
  private intervalId: any;
  private statsSubscription?: Subscription;
  private newsSubscription?: Subscription;
  
  noticias: Noticia[] = [];
  jornadasActivas: Jornada[] = [];
  jornadasFinalizadas: Jornada[] = [];
  
  stats: ImpactStats = { 
    municipiosVisitados: 0, 
    personasAtendidas: 0,
    personasActivas: 0,
    personasRegistradas: 0,
    totalAsistencias: 0,
    totalEstudiantes: 0
  };
  
  images = [
    'https://images.unsplash.com/photo-1541339907198-e08756dedf3f?q=80&w=2070&auto=format&fit=crop',
    'https://images.unsplash.com/photo-1517486808906-6ca8b3f04846?q=80&w=2070&auto=format&fit=crop',
    'https://images.unsplash.com/photo-1523240795612-9a054b0db644?q=80&w=2070&auto=format&fit=crop',
    'https://images.unsplash.com/photo-1552664730-d307ca884978?q=80&w=2070&auto=format&fit=crop'
  ];

  facultades = [
    { nombre: 'Facultad de Ingenierías', icon: 'fas fa-cogs', color: '#e3000f', desc: 'Soluciones tecnológicas e innovación. Proyectos orientados a resolver problemas técnicos y de infraestructura en las comunidades.', url: 'https://www.uniremington.edu.co/facultades/facultad-de-ingenierias/', expanded: false },
    { nombre: 'Facultad de Ciencias de la Salud', icon: 'fas fa-heartbeat', color: '#00b359', desc: 'Cuidado y bienestar integral. Jornadas de prevención, promoción de la salud y atención básica a poblaciones vulnerables.', url: 'https://www.uniremington.edu.co/facultades/facultad-de-ciencias-de-la-salud/', expanded: false },
    { nombre: 'Facultad de Diseño', icon: 'fas fa-palette', color: '#ffcc00', desc: 'Expresión creativa y visual. Intervenciones artísticas, talleres creativos y mejoramiento estético de espacios públicos.', url: 'https://www.uniremington.edu.co/facultades/facultad-de-diseno/', expanded: false },
    { nombre: 'Facultad de Ciencias Jurídicas y Políticas', icon: 'fas fa-balance-scale', color: '#0052cc', desc: 'Justicia y acompañamiento legal. Asesoría jurídica gratuita y resolución de conflictos para la comunidad.', url: 'https://www.uniremington.edu.co/facultades/facultad-de-ciencias-juridicas-y-politicas/', expanded: false },
    { nombre: 'Facultad de Ciencias Empresariales', icon: 'fas fa-briefcase', color: '#e6005c', desc: 'Desarrollo de negocios. Apoyo a emprendedores locales, asesoría en planes de negocio y formalización.', url: 'https://www.uniremington.edu.co/facultades/facultad-de-ciencias-empresariales/', expanded: false },
    { nombre: 'Facultad de Medicina Veterinaria', icon: 'fas fa-paw', color: '#8e44ad', desc: 'Salud animal y ecosistemas. Jornadas de vacunación, desparasitación y concientización sobre el cuidado de mascotas.', url: 'https://www.uniremington.edu.co/facultades/facultad-medicina-veterinaria/', expanded: false },
    { nombre: 'Facultad de Ciencias Contables', icon: 'fas fa-chart-pie', color: '#0099cc', desc: 'Transparencia financiera. Orientación contable, educación financiera y asesoría tributaria para pequeños negocios y familias.', url: 'https://www.uniremington.edu.co/facultades/facultad-de-ciencias-contables/', expanded: false }
  ];

  constructor(
    private router: Router,
    private statsService: StatsService,
    private newsService: NewsService,
    private jornadaService: JornadaService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
  }

  ngOnInit() {
    this.startAutoPlay();
    if (isPlatformBrowser(this.platformId)) {
      // Polling de estadísticas
      this.statsSubscription = interval(10000)
        .pipe(
          startWith(0),
          switchMap(() => this.statsService.getImpactStats())
        )
        .subscribe({
          next: (res) => this.stats = res,
          error: (err) => console.error('Error fetching stats', err)
        });

      // Cargar noticias y jornadas
      this.loadNews();
      this.loadJornadas();
    }
  }

  loadJornadas() {
    this.jornadaService.getJornadas().subscribe({
      next: (jornadas) => {
        this.jornadasActivas = jornadas.filter(j => j.estado !== 'FINALIZADA' && j.estado !== 'CANCELADA');
        this.jornadasFinalizadas = jornadas.filter(j => j.estado === 'FINALIZADA');
      },
      error: (err) => console.error('Error loading jornadas', err)
    });
  }

  loadNews() {
    this.newsService.getNoticias().subscribe({
      next: (res) => {
        this.noticias = res;
        if (this.noticias.length === 0) {
          this.noticias = [{
            id: 'mock',
            titulo: '¡Uniremington al Parque llega a tu municipio!',
            contenido: 'Estamos emocionados de anunciar que nuestra próxima jornada de servicios gratuitos será este fin de semana. ¡Te esperamos!',
            imageUrl: 'https://images.unsplash.com/photo-1517486808906-6ca8b3f04846?q=80&w=2070&auto=format&fit=crop',
            fechaPublicacion: new Date().toISOString(),
            autor: 'Administración'
          }];
        }
      },
      error: (err) => console.error('Error loading news', err)
    });
  }

  ngOnDestroy() {
    this.stopAutoPlay();
    if (this.statsSubscription) {
      this.statsSubscription.unsubscribe();
    }
  }

  startAutoPlay() {
    this.intervalId = setInterval(() => {
      this.nextSlide();
    }, 5000);
  }

  stopAutoPlay() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  nextSlide() {
    this.currentSlide = (this.currentSlide + 1) % this.images.length;
  }

  prevSlide() {
    this.currentSlide = (this.currentSlide - 1 + this.images.length) % this.images.length;
  }

  goToSlide(index: number) {
    this.currentSlide = index;
    this.stopAutoPlay();
    this.startAutoPlay();
  }

  toggleFacultad(f: any) {
    f.expanded = !f.expanded;
  }

}

