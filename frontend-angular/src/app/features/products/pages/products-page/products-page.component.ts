
import { Component, OnInit } from '@angular/core';
import { Product, ProductRequest } from '../../models/product.model';
import { ViewState } from '../../../../shared/models/view-state';
import { SaleItem, SaleRequest } from '../../../sales/models/sale.model';
import { ProductService } from '../../services/product.service';
import { SaleService } from '../../../sales/services/sale.service';
import { ApiClientError } from '../../../../core/models/api-error.model';

@Component({
  selector: 'app-products-page',
  templateUrl: './products-page.component.html',
  styleUrls: ['./products-page.component.css']
})
export class ProductsPageComponent implements OnInit {
  products: readonly Product[] = [];
  listState: ViewState = 'loading';
  listMessage = '';

  formState: ViewState = 'idle';
  formMessage = '';
  preparedProduct: ProductRequest | null = null;

  items: SaleItem[] = [];
  saleState: ViewState = 'idle';
  saleMessage = '';
  preparedSale: SaleRequest | null = null;

  constructor(
    private readonly productService: ProductService,
    private readonly saleService: SaleService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.listState = 'loading';
    this.listMessage = 'Consultando el catálogo de productos...';

    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.listState = products.length > 0 ? 'success' : 'empty';
        this.listMessage = '';
      },
      error: (error: ApiClientError) => {
        this.listState = 'error';
        this.listMessage = `${error.title}: ${error.detail}`;
      }
    });
  }

  onProductPrepared(product: ProductRequest): void {
    this.preparedProduct = product;
    this.formState = 'loading';
    this.formMessage = 'Registrando producto...';

    this.productService.createProduct(product).subscribe({
      next: (createdProduct) => {
        this.preparedProduct = null;
        this.formState = 'success';
        this.formMessage =
          `Producto ${createdProduct.code} registrado correctamente.`;

        this.loadProducts();
      },
      error: (error: ApiClientError) => {
        this.formState = 'error';
        this.formMessage = `${error.title}: ${error.detail}`;
      }
    });
  }

  addProduct(product: Product): void {
    const existing = this.items.find(
      (item) => item.productCode === product.code
    );

    if (existing) {
      this.items = this.items.map((item) => (
        item.productCode === product.code
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      this.items = [
        ...this.items,
        { productCode: product.code, quantity: 1 }
      ];
    }

    this.resetSalePreparation();
  }

  updateCartItem(updatedItem: SaleItem): void {
    if (!Number.isInteger(updatedItem.quantity) || updatedItem.quantity <= 0) {
      return;
    }

    this.items = this.items.map((item) => (
      item.productCode === updatedItem.productCode ? updatedItem : item
    ));

    this.resetSalePreparation();
  }

  removeCartItem(productCode: string): void {
    this.items = this.items.filter(
      (item) => item.productCode !== productCode
    );

    this.resetSalePreparation();
  }

  onSalePrepared(sale: SaleRequest): void {
    this.preparedSale = sale;
    this.saleState = 'loading';
    this.saleMessage = 'Procesando venta...';

    this.saleService.processSale(sale).subscribe({
      next: (response) => {
        this.saleState = 'success';
        this.saleMessage =
          `Venta registrada correctamente. Total: $${response.total.toFixed(2)}.`;

        this.items = [];
        this.preparedSale = null;

        // Actualiza las existencias después de la venta.
        this.loadProducts();
      },
      error: (error: ApiClientError) => {
        this.saleState = 'error';
        this.saleMessage = `${error.title}: ${error.detail}`;
      }
    });
  }

  private resetSalePreparation(): void {
    this.saleState = 'idle';
    this.saleMessage = '';
    this.preparedSale = null;
  }
}
