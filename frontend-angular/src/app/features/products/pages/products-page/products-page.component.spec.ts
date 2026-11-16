import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { ProductsPageComponent } from './products-page.component';
import { ProductFormComponent } from '../../components/product-form/product-form.component';
import { ProductListComponent } from '../../components/product-list/product-list.component';
import { CartComponent } from '../../../sales/components/cart/cart.component';
import { StateMessageComponent } from '../../../../shared/components/state-message/state-message.component';

describe('ProductsPageComponent', () => {
  let component: ProductsPageComponent;
  let fixture: ComponentFixture<ProductsPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProductsPageComponent,
        ProductFormComponent,
        ProductListComponent,
        CartComponent,
        StateMessageComponent
      ],
      imports: [
  ReactiveFormsModule,
  HttpClientTestingModule
]});
    fixture = TestBed.createComponent(ProductsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('adds selected products and increments their quantity', () => {
    const product = { code: 'P00001', name: 'Café', price: 12.5, stock: 4 };

    component.addProduct(product);
    component.addProduct(product);

    expect(component.items).toEqual([{ productCode: 'P00001', quantity: 2 }]);
  });

  it('updates and removes cart items', () => {
    component.items = [{ productCode: 'P00001', quantity: 1 }];

    component.updateCartItem({ productCode: 'P00001', quantity: 3 });
    expect(component.items).toEqual([{ productCode: 'P00001', quantity: 3 }]);

    component.removeCartItem('P00001');
    expect(component.items).toEqual([]);
  });
});
