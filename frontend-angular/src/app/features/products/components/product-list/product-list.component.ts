import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Product } from '../../models/product.model';
import { ViewState } from '../../../../shared/models/view-state';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent {
  @Input() products: readonly Product[] = [];
  @Input() state: ViewState = 'empty';
  @Output() readonly productSelected = new EventEmitter<Product>();

  addToCart(product: Product): void {
    this.productSelected.emit(product);
  }
}
