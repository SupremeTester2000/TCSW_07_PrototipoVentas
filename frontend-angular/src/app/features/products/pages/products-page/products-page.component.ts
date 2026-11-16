import { Component } from '@angular/core';
import { Product, ProductRequest } from '../../models/product.model';
import { ViewState } from '../../../../shared/models/view-state';
import { SaleItem, SaleRequest } from '../../../sales/models/sale.model';

@Component({
  selector: 'app-products-page',
  templateUrl: './products-page.component.html',
  styleUrls: ['./products-page.component.css']
})
export class ProductsPageComponent {
  readonly products: readonly Product[] = [];
  readonly listState: ViewState = 'empty';
  formState: ViewState = 'idle';
  formMessage = '';
  preparedProduct: ProductRequest | null = null;
  items: SaleItem[] = [];
  saleState: ViewState = 'idle';
  saleMessage = '';
  preparedSale: SaleRequest | null = null;

  onProductPrepared(product: ProductRequest): void {
    this.preparedProduct = product;
    this.formState = 'success';
    this.formMessage = 'Formulario validado. El producto está listo, pero todavía no se ha enviado al servidor.';
  }

  addProduct(product: Product): void {
    const existing = this.items.find((item) => item.productCode === product.code);
    if (existing) {
      this.items = this.items.map((item) => (
        item.productCode === product.code
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      this.items = [...this.items, { productCode: product.code, quantity: 1 }];
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
    this.items = this.items.filter((item) => item.productCode !== productCode);
    this.resetSalePreparation();
  }

  onSalePrepared(sale: SaleRequest): void {
    this.preparedSale = sale;
    this.saleState = 'success';
    this.saleMessage = 'Venta preparada. Aún no se ha enviado al servidor.';
  }

  private resetSalePreparation(): void {
    this.saleState = 'idle';
    this.saleMessage = '';
    this.preparedSale = null;
  }
}
