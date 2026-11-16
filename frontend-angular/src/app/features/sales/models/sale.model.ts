/**
 * Elemento utilizado internamente por el carrito.
 */
export interface SaleItem {
  productCode: string;
  quantity: number;
}

/**
 * Información enviada a Spring Boot.
 * Las claves representan códigos de productos
 * y los valores representan cantidades.
 */
export type SaleRequest = Record<string, number>;

/**
 * Detalle de cada producto incluido en la venta.
 */
export interface SaleLine {
  productCode: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

/**
 * Respuesta de Spring Boot al procesar una venta.
 */
export interface SaleResponse {
  lines: SaleLine[];
  subtotal: number;
  discount: number;
  taxes: number;
  total: number;
}