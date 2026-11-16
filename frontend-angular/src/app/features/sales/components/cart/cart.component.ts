import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SaleItem, SaleRequest } from '../../models/sale.model';
import { ViewState } from '../../../../shared/models/view-state';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent {
  @Input() items: readonly SaleItem[] = [];
  @Output() readonly itemRemoved = new EventEmitter<string>();
  @Output() readonly salePrepared = new EventEmitter<SaleRequest>();

  get state(): ViewState {
    return this.items.length === 0 ? 'empty' : 'idle';
  }

  remove(productCode: string): void {
    this.itemRemoved.emit(productCode);
  }

  prepareSale(): void {
    if (this.items.length === 0) {
      return;
    }

    const saleRequest: SaleRequest = {};
    for (const item of this.items) {
      saleRequest[item.productCode] = item.quantity;
    }
    this.salePrepared.emit(saleRequest);
  }
}
