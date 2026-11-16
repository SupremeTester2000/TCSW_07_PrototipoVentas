import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { SalesPageComponent } from './sales-page.component';
import { SaleFormComponent } from '../../components/sale-form/sale-form.component';
import { CartComponent } from '../../components/cart/cart.component';
import { StateMessageComponent } from '../../../../shared/components/state-message/state-message.component';
import { SaleItem, SaleRequest } from '../../models/sale.model';

describe('SalesPageComponent', () => {
  let component: SalesPageComponent;
  let fixture: ComponentFixture<SalesPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        SalesPageComponent,
        SaleFormComponent,
        CartComponent,
        StateMessageComponent
      ],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SalesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add a new item to the sale', () => {
    const item: SaleItem = {
      productCode: 'P00001',
      quantity: 2
    };

    component.addItem(item);

    expect(component.items).toEqual([
      { productCode: 'P00001', quantity: 2 }
    ]);
  });

  it('should increase the quantity when adding an existing item', () => {
    component.addItem({
      productCode: 'P00001',
      quantity: 2
    });

    component.addItem({
      productCode: 'P00001',
      quantity: 3
    });

    expect(component.items).toEqual([
      { productCode: 'P00001', quantity: 5 }
    ]);
  });

  it('should update the quantity of an existing item', () => {
    component.items = [
      { productCode: 'P00001', quantity: 1 }
    ];

    component.updateItemQuantity({
      productCode: 'P00001',
      quantity: 4
    });

    expect(component.items).toEqual([
      { productCode: 'P00001', quantity: 4 }
    ]);
  });

  it('should ignore invalid quantities', () => {
    component.items = [
      { productCode: 'P00001', quantity: 2 }
    ];

    component.updateItemQuantity({
      productCode: 'P00001',
      quantity: 0
    });

    expect(component.items).toEqual([
      { productCode: 'P00001', quantity: 2 }
    ]);
  });

  it('should remove an item from the sale', () => {
    component.items = [
      { productCode: 'P00001', quantity: 2 },
      { productCode: 'P00002', quantity: 1 }
    ];

    component.removeItem('P00001');

    expect(component.items).toEqual([
      { productCode: 'P00002', quantity: 1 }
    ]);
  });

  it('should start processing a sale', () => {
    const sale: SaleRequest = {
      P00001: 2,
      P00002: 1
    };

    component.onSalePrepared(sale);

    expect(component.preparedSale).toEqual(sale);
    expect(component.saleState).toBe('loading');
    expect(component.saleMessage).toBe('Procesando venta...');
  });

  it('should reset the sale preparation when adding an item', () => {
    component.onSalePrepared({ P00001: 1 });

    component.addItem({
      productCode: 'P00002',
      quantity: 1
    });

    expect(component.saleState).toBe('idle');
    expect(component.saleMessage).toBe('');
    expect(component.preparedSale).toBeNull();
  });

  it('should reset the sale preparation when updating an item', () => {
    component.items = [
      { productCode: 'P00001', quantity: 1 }
    ];

    component.onSalePrepared({ P00001: 1 });

    component.updateItemQuantity({
      productCode: 'P00001',
      quantity: 3
    });

    expect(component.saleState).toBe('idle');
    expect(component.saleMessage).toBe('');
    expect(component.preparedSale).toBeNull();
  });
});