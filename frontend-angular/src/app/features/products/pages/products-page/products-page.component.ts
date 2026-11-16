import { Component } from '@angular/core';
import { Product, ProductRequest } from '../../models/product.model';
import { ViewState } from '../../../../shared/models/view-state';

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

  onProductPrepared(product: ProductRequest): void {
    this.preparedProduct = product;
    this.formState = 'success';
    this.formMessage = 'Formulario validado. El producto está listo, pero todavía no se ha enviado al servidor.';
  }
}
