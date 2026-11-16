import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductListComponent } from './product-list.component';
import { StateMessageComponent } from '../../../../shared/components/state-message/state-message.component';

describe('ProductListComponent', () => {
  let component: ProductListComponent;
  let fixture: ComponentFixture<ProductListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductListComponent, StateMessageComponent]
    });
    fixture = TestBed.createComponent(ProductListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('renders the REST product fields and emits a selected product', () => {
    const product = { code: 'P00001', name: 'Café', price: 12.5, stock: 4 };
    const emitSpy = spyOn(component.productSelected, 'emit');
    component.products = [product];

    fixture.detectChanges();

    const renderedText = fixture.nativeElement.textContent as string;
    expect(renderedText).toContain('P00001');
    expect(renderedText).toContain('Café');
    expect(renderedText).toContain('12.50');
    expect(renderedText).toContain('Existencia');
    expect(renderedText).toContain('4');

    component.addToCart(product);
    expect(emitSpy).toHaveBeenCalledOnceWith(product);
  });
});
