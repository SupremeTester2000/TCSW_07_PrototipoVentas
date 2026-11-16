export interface ProductRequest {
  name: string;
  price: number;
  stock: number;
}

export interface ProductResponse {
  code: string;
  name: string;
  price: number;
  stock: number;
}

/**
 * Alias conservado para mantener compatibilidad
 * con los componentes existentes.
 */
export type Product = ProductResponse;