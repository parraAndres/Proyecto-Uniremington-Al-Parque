import Dexie, { Table } from 'dexie';

export interface BeneficiaryRecord {
  id: string; // uuid
  nombres: string;
  apellidos: string;
  documento: string;
  edad: number;
  genero: string;
  telefono: string;
  municipio: string;
  barrio: string;
  tipoPoblacion: string;
  servicioSolicitado: string;
  autorizacionDatos: boolean;
}

export interface UserRecord {
  email: string;
  documento?: string;
  nombreCompleto: string;
  facultad?: string;
  programa?: string;
  passwordHash: string;
  role: 'admin' | 'estudiante' | 'cliente' | 'profesor';
}

export interface ServicioRecord {
  id: string; // uuid
  beneficiarioId: string;
  tipoServicio: string;
  facultadResponsable: string;
  descripcion: string;
  tiempoAtencion: number; // minutos
  resultado: string; // orientacion, intervencion, remision
  observaciones: string;
}

export interface SeguimientoRecord {
  id: string;
  beneficiarioId: string;
  estado: string; // abierto, proceso, cerrado
  fechaProgramada: string;
  avances: string;
}

export interface DiagnosticoRecord {
  id: string;
  problematica: string;
  clasificacion: string; // salud, legal, productivo, ambiental
  prioridad: string;
}

export interface AcademicoRecord {
  id: string;
  estudiante: string;
  programa: string;
  horas: number;
}

export interface RecursoRecord {
  id: string;
  aporte: string;
  tipo: string; // dinero, especie
  valorEstimado?: number;
}

export class AppDB extends Dexie {
  beneficiaries!: Table<BeneficiaryRecord, string>;
  users!: Table<UserRecord, string>;
  servicios!: Table<ServicioRecord, string>;
  seguimientos!: Table<SeguimientoRecord, string>;
  diagnosticos!: Table<DiagnosticoRecord, string>;
  academico!: Table<AcademicoRecord, string>;
  recursos!: Table<RecursoRecord, string>;

  constructor() {
    super('UniremingtonParqueDB_v2');
    this.version(4).stores({
      beneficiaries: 'id, documento, municipio',
      users: 'email',
      servicios: 'id, beneficiarioId, facultadResponsable',
      seguimientos: 'id, beneficiarioId, estado',
      diagnosticos: 'id, clasificacion, prioridad',
      academico: 'id, estudiante, programa',
      recursos: 'id, tipo'
    });
  }
}

export const db = new AppDB();
