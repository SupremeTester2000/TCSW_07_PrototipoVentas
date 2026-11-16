import { Component } from '@angular/core';
import { SaleItem, SaleRequest } from '../../models/sale.model';
import { ViewState } from '../../../../shared/models/view-state';

@Component({
  selector: 'app-sales-page',
  templateUrl: './sales-page.component.html',
  styleUrls: ['./sales-page.component.css']
})
export class SalesPageComponent {
  items: SaleItem[] = [];
  saleState: ViewState = 'idle';
  saleMessage = '';
  preparedSale: SaleRequest | null = null;

  addItem(item: SaleItem): void {
    const existing = this.items.find((current) => current.productCode === item.productCode);
    if (existing) {
      existing.quantity += item.quantity;
    } else {
      this.items = [...this.items, item];
    }
    this.saleState = 'idle';
    this.saleMessage = '';
    this.preparedSale = null;
  }

  removeItem(productCode: string): void {
    this.items = this.items.filter((item) => item.productCode !== productCode);
    this.saleState = 'idle';
    this.saleMessage = '';
    this.preparedSale = null;
  }

  onSalePrepared(sale: SaleRequest): void {
    this.preparedSale = sale;
    this.saleState = 'success';
    this.saleMessage = 'Venta preparada. Aún no se ha enviado al servidor.';
  }
}
