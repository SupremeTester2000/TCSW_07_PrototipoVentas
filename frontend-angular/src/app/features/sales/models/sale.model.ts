export interface SaleItem {
  productCode: string;
  quantity: number;
}

export type SaleRequest = Record<string, number>;
