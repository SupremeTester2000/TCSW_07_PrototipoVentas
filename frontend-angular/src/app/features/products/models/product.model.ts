export interface ProductRequest {
  name: string;
  price: number;
  stock: number;
}

export interface Product extends ProductRequest {
  code: string;
}
