import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

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
  
  images = [
    'https://images.unsplash.com/photo-1541339907198-e08756dedf3f?q=80&w=2070&auto=format&fit=crop',
    'https://images.unsplash.com/photo-1517486808906-6ca8b3f04846?q=80&w=2070&auto=format&fit=crop',
    'https://images.unsplash.com/photo-1523240795612-9a054b0db644?q=80&w=2070&auto=format&fit=crop',
    'https://images.unsplash.com/photo-1552664730-d307ca884978?q=80&w=2070&auto=format&fit=crop'
  ];

  facultades = [
    { nombre: 'Ingeniería', icon: '⚙️', color: '#ff6b00', desc: 'Soluciones tecnológicas e innovación. Proyectos orientados a resolver problemas técnicos y de infraestructura en las comunidades.', expanded: false },
    { nombre: 'Salud', icon: '⚕️', color: '#00b359', desc: 'Cuidado y bienestar integral. Jornadas de prevención, promoción de la salud y atención básica a poblaciones vulnerables.', expanded: false },
    { nombre: 'Artes y Diseño', icon: '🎨', color: '#ffcc00', desc: 'Expresión creativa y visual. Intervenciones artísticas, talleres creativos y mejoramiento estético de espacios públicos.', expanded: false },
    { nombre: 'Ciencias Jurídicas', icon: '⚖️', color: '#0052cc', desc: 'Justicia y acompañamiento legal. Asesoría jurídica gratuita y resolución de conflictos para la comunidad.', expanded: false },
    { nombre: 'Empresariales', icon: '💼', color: '#e6005c', desc: 'Desarrollo de negocios. Apoyo a emprendedores locales, asesoría en planes de negocio y formalización.', expanded: false },
    { nombre: 'Veterinaria', icon: '🐾', color: '#8e44ad', desc: 'Salud animal y ecosistemas. Jornadas de vacunación, desparasitación y concientización sobre el cuidado de mascotas.', expanded: false },
    { nombre: 'Contaduría', icon: '📊', color: '#0099cc', desc: 'Transparencia financiera. Orientación contable, educación financiera y asesoría tributaria para pequeños negocios y familias.', expanded: false }
  ];

  constructor(
    private router: Router
  ) {
  }

  ngOnInit() {
    this.startAutoPlay();
  }

  ngOnDestroy() {
    this.stopAutoPlay();
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
