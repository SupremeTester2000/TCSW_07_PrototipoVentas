
import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import {
  ApiClientError,
  ApiError
} from '../models/api-error.model';

@Injectable({
  providedIn: 'root'
})
export class ApiErrorService {

  /**
   * Distingue errores de red, errores HTTP y errores inesperados.
   * Conserva siempre el error original para facilitar su diagnóstico.
   */
  normalize(error: unknown): ApiClientError {

    if (error instanceof HttpErrorResponse) {

      // Status 0 indica normalmente un fallo de conexión,
      // un problema de red o una solicitud bloqueada por el navegador.
      if (error.status === 0) {
        return new ApiClientError(
          'network',
          0,
          'Error de conexión',
          'No fue posible comunicarse con el servidor. Compruebe la conexión y que la API esté disponible.',
          error
        );
      }

      // Utiliza el formato definido por OpenAPI cuando sea válido.
      const apiError = this.isApiError(error.error)
        ? error.error
        : null;

      return new ApiClientError(
        'http',
        error.status,
        apiError?.title ?? `Error HTTP ${error.status}`,
        apiError?.detail ??
          `El servidor respondió con el código HTTP ${error.status}, pero no proporcionó un detalle de error válido.`,
        error
      );
    }

    // Conserva información de errores que no proceden de HttpClient.
    const detail = error instanceof Error
      ? error.message
      : 'Se produjo un error inesperado en el cliente.';

    return new ApiClientError(
      'unexpected',
      null,
      'Error inesperado',
      detail,
      error
    );
  }

  /**
   * Comprueba en tiempo de ejecución que el cuerpo recibido
   * realmente cumple la estructura ApiError.
   */
  private isApiError(value: unknown): value is ApiError {

    if (typeof value !== 'object' || value === null) {
      return false;
    }

    const candidate = value as Record<string, unknown>;

    return (
      typeof candidate['title'] === 'string' &&
      typeof candidate['status'] === 'number' &&
      typeof candidate['detail'] === 'string'
    );
  }
}
