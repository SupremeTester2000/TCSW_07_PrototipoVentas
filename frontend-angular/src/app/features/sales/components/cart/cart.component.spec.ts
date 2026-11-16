import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CartComponent } from './cart.component';
import { StateMessageComponent } from '../../../../shared/components/state-message/state-message.component';

describe('CartComponent', () => {
  let component: CartComponent;
  let fixture: ComponentFixture<CartComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CartComponent, StateMessageComponent]
    });
    fixture = TestBed.createComponent(CartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('emits the sale request shape defined by the API', () => {
    const emitSpy = spyOn(component.salePrepared, 'emit');
    component.items = [
      { productCode: 'P00001', quantity: 2 },
      { productCode: 'P00002', quantity: 1 }
    ];

    component.prepareSale();

    expect(emitSpy).toHaveBeenCalledOnceWith({ P00001: 2, P00002: 1 });
  });

  it('does not prepare an empty sale', () => {
    const emitSpy = spyOn(component.salePrepared, 'emit');

    component.prepareSale();

    expect(emitSpy).not.toHaveBeenCalled();
  });
});
