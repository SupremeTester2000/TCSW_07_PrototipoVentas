
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiErrorService } from '../../../core/services/api-error.service';
import {
  ProductRequest,
  ProductResponse
} from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private readonly endpoint = `${API_BASE_URL}/api/products`;

  constructor(
    private readonly http: HttpClient,
    private readonly apiErrorService: ApiErrorService
  ) {}

  /**
   * Consulta el catálogo completo de productos.
   */
  getProducts(): Observable<ProductResponse[]> {
    return this.http.get<ProductResponse[]>(this.endpoint).pipe(
      catchError(error => this.handleError(error))
    );
  }

  /**
   * Registra un producto.
   * Spring Boot responde con HTTP 201.
   */
  createProduct(product: ProductRequest): Observable<ProductResponse> {
    return this.http.post<ProductResponse>(
      this.endpoint,
      product
    ).pipe(
      catchError(error => this.handleError(error))
    );
  }

  /**
   * Actualiza un producto existente.
   */
  updateProduct(
    code: string,
    product: ProductRequest
  ): Observable<ProductResponse> {
    const url = `${this.endpoint}/${encodeURIComponent(code)}`;

    return this.http.put<ProductResponse>(
      url,
      product
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
