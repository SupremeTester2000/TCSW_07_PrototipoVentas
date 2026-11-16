
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiErrorService } from '../../../core/services/api-error.service';
import {
  SaleRequest,
  SaleResponse
} from '../models/sale.model';

@Injectable({
  providedIn: 'root'
})
export class SaleService {

  private readonly endpoint = `${API_BASE_URL}/api/sales`;

  constructor(
    private readonly http: HttpClient,
    private readonly apiErrorService: ApiErrorService
  ) {}

  /**
   * Envía una venta a Spring Boot.
   */
  processSale(sale: SaleRequest): Observable<SaleResponse> {
    return this.http.post<SaleResponse>(
      this.endpoint,
      sale
    ).pipe(
      catchError(error => this.handleError(error))
    );
  }

  /**
   * Normaliza los errores sin ocultar su información original.
   */
  private handleError(error: unknown): Observable<never> {
    return throwError(() => this.apiErrorService.normalize(error));
  }
}
