
import { Component } from '@angular/core';
import { SaleItem, SaleRequest } from '../../models/sale.model';
import { ViewState } from '../../../../shared/models/view-state';
import { SaleService } from '../../services/sale.service';
import { ApiClientError } from '../../../../core/models/api-error.model';

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

  constructor(private readonly saleService: SaleService) {}

  addItem(item: SaleItem): void {
    const existing = this.items.find(
      (current) => current.productCode === item.productCode
    );

    if (existing) {
      this.items = this.items.map((current) => (
        current.productCode === item.productCode
          ? { ...current, quantity: current.quantity + item.quantity }
          : current
      ));
    } else {
      this.items = [...this.items, item];
    }

    this.resetSalePreparation();
  }

  updateItemQuantity(updatedItem: SaleItem): void {
    if (!Number.isInteger(updatedItem.quantity) || updatedItem.quantity <= 0) {
      return;
    }

    this.items = this.items.map((item) => (
      item.productCode === updatedItem.productCode ? updatedItem : item
    ));

    this.resetSalePreparation();
  }

  removeItem(productCode: string): void {
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
