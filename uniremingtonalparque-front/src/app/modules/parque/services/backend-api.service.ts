import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BackendApiService {
  pushBeneficiary(data: any): Observable<any> {
    console.log('Pushing data to backend', data);
    return of({ success: true });
  }
}
