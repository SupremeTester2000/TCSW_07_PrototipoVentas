import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { SalesPageComponent } from './sales-page.component';
import { SaleFormComponent } from '../../components/sale-form/sale-form.component';
import { CartComponent } from '../../components/cart/cart.component';
import { StateMessageComponent } from '../../../../shared/components/state-message/state-message.component';

describe('SalesPageComponent', () => {
  let component: SalesPageComponent;
  let fixture: ComponentFixture<SalesPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        SalesPageComponent,
        SaleFormComponent,
        CartComponent,
        StateMessageComponent
      ],
      imports: [ReactiveFormsModule]
    });
    fixture = TestBed.createComponent(SalesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('updates item quantities from the cart', () => {
    component.items = [{ productCode: 'P00001', quantity: 1 }];

    component.updateItemQuantity({ productCode: 'P00001', quantity: 4 });

    expect(component.items).toEqual([{ productCode: 'P00001', quantity: 4 }]);
  });
});
