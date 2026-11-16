
/**
 * Estructura de error definida en el contrato OpenAPI.
 */
export interface ApiError {
  title: string;
  status: number;
  detail: string;
}

/**
 * Categorías de errores reconocidas por el cliente.
 */
export type ApiClientErrorKind = 'network' | 'http' | 'unexpected';

/**
 * Error normalizado para comunicar fallos sin ocultar
 * la información original de HttpClient.
 */
export class ApiClientError extends Error {
  constructor(
    public readonly kind: ApiClientErrorKind,
    public readonly status: number | null,
    public readonly title: string,
    public readonly detail: string,
    public readonly originalError: unknown
  ) {
    super(detail);

    this.name = 'ApiClientError';
    Object.setPrototypeOf(this, ApiClientError.prototype);
  }
}
