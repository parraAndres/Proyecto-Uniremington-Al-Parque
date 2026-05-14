import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { NewsService, Noticia } from '../../../core/services/news.service';

@Component({
  selector: 'app-noticia-detalle',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="noticia-page" *ngIf="noticia">
      <header class="noticia-header" [style.background-image]="'url(' + (noticia.imageUrl || 'https://images.unsplash.com/photo-1541339907198-e08756dedf3f?auto=format&fit=crop&w=1200') + ')'">
        <div class="overlay"></div>
        <div class="container header-content">
          <a routerLink="/" class="btn-back"><i class="fas fa-arrow-left"></i> Volver</a>
          <h1>{{ noticia.titulo }}</h1>
          <div class="meta">
             <span><i class="far fa-calendar-alt"></i> {{ noticia.fechaPublicacion | date:'dd MMMM, yyyy' }}</span>
             <span><i class="fas fa-user"></i> {{ noticia.autor || 'Redacción Uniremington' }}</span>
          </div>
        </div>
      </header>

      <article class="container noticia-body">
        <div class="content-wrapper">
           <div class="full-text" [innerHTML]="formattedContent"></div>
        </div>
      </article>
    </div>

    <div class="loading-state" *ngIf="isLoading">
       <div class="loader"></div>
       <p>Cargando noticia...</p>
    </div>

    <div class="error-state" *ngIf="errorMessage && !isLoading">
       <i class="fas fa-exclamation-circle"></i>
       <p>{{ errorMessage }}</p>
       <a routerLink="/" class="btn-retry">Volver al inicio</a>
    </div>
  `,
  styles: [`
    .noticia-page {
      min-height: 100vh;
      background: #f8f9fa;
    }
    .noticia-header {
      height: 400px;
      background-size: cover;
      background-position: center;
      position: relative;
      display: flex;
      align-items: flex-end;
      padding-bottom: 3rem;
      color: white;

      .overlay {
        position: absolute;
        top: 0; left: 0; right: 0; bottom: 0;
        background: linear-gradient(to bottom, rgba(0,0,0,0.2), rgba(0,0,0,0.8));
      }

      .header-content {
        position: relative;
        z-index: 1;
        
        h1 {
          font-size: 3rem;
          font-weight: 800;
          margin: 1rem 0;
          text-shadow: 0 2px 10px rgba(0,0,0,0.5);
        }

        .meta {
          display: flex;
          gap: 2rem;
          font-size: 1.1rem;
          opacity: 0.9;
        }

        .btn-back {
          color: white;
          text-decoration: none;
          display: inline-flex;
          align-items: center;
          gap: 0.5rem;
          background: rgba(255,255,255,0.2);
          padding: 0.5rem 1rem;
          border-radius: 20px;
          backdrop-filter: blur(5px);
          transition: all 0.3s;
          margin-bottom: 1rem;

          &:hover {
            background: #ea0a2a;
          }
        }
      }
    }

    .noticia-body {
      padding: 4rem 0;
      
      .content-wrapper {
        background: white;
        padding: 4rem;
        border-radius: 12px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.05);
        margin-top: -100px;
        position: relative;
        z-index: 2;
        max-width: 900px;
        margin-left: auto;
        margin-right: auto;
      }

      .full-text {
        font-size: 1.25rem;
        line-height: 1.8;
        color: #333;
        white-space: pre-wrap;
      }
    }

    .loading-state, .error-state {
      height: 60vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      text-align: center;
      padding: 2rem;
    }

    .error-state {
      i {
        font-size: 4rem;
        color: #ea0a2a;
        margin-bottom: 1.5rem;
      }
      p {
        font-size: 1.2rem;
        color: #666;
        margin-bottom: 2rem;
      }
      .btn-retry {
        background: #00447b;
        color: white;
        padding: 0.8rem 2rem;
        border-radius: 30px;
        text-decoration: none;
        transition: transform 0.3s;
        &:hover { transform: scale(1.05); }
      }
    }

    @media (max-width: 768px) {
      .noticia-header {
        height: 300px;
        h1 { font-size: 2rem; }
      }
      .noticia-body .content-wrapper {
        padding: 2rem;
        margin-top: -50px;
      }
    }
  `]
})
export class NoticiaDetalleComponent implements OnInit {
  noticia?: Noticia;
  isLoading = true;
  errorMessage?: string;

  constructor(
    private route: ActivatedRoute,
    private newsService: NewsService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    console.log('Cargando noticia con ID:', id);

    if (id && id !== 'undefined' && id !== 'null') {
      this.newsService.getNoticiaById(id).subscribe({
        next: (n) => {
          console.log('Noticia cargada:', n);
          this.noticia = n;
          this.isLoading = false;
        },
        error: (err: any) => {
          console.error('Error al cargar la noticia:', err);
          this.errorMessage = `Error ${err.status || ''}: ${err.message || 'No se pudo cargar la noticia'}`;
          this.isLoading = false;
        }
      });
    } else {
      this.errorMessage = 'ID de noticia no válido';
      this.isLoading = false;
    }
  }

  get formattedContent() {
    if (!this.noticia?.contenido) return '';
    // Convertir saltos de línea en <p> o <br> si es necesario, 
    // pero el pre-wrap ya ayuda.
    return this.noticia.contenido;
  }
}
