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
  @Output() readonly quantityChanged = new EventEmitter<SaleItem>();
  @Output() readonly salePrepared = new EventEmitter<SaleRequest>();

  get state(): ViewState {
    return this.items.length === 0 ? 'empty' : 'idle';
  }

  get totalQuantity(): number {
    return this.items.reduce((total, item) => total + item.quantity, 0);
  }

  updateQuantity(item: SaleItem, quantity: number, input: HTMLInputElement): void {
    if (!Number.isInteger(quantity) || quantity <= 0) {
      input.value = String(item.quantity);
      return;
    }

    this.quantityChanged.emit({ ...item, quantity });
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
