import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { SaleFormComponent } from './sale-form.component';

describe('SaleFormComponent', () => {
  let component: SaleFormComponent;
  let fixture: ComponentFixture<SaleFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SaleFormComponent],
      imports: [ReactiveFormsModule]
    });
    fixture = TestBed.createComponent(SaleFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('rejects codes that do not match the API format and non-positive quantities', () => {
    component.form.setValue({ productCode: 'P1234', quantity: 0 });

    expect(component.form.invalid).toBeTrue();
    expect(component.form.controls.productCode.hasError('pattern')).toBeTrue();
    expect(component.form.controls.quantity.hasError('min')).toBeTrue();
  });

  it('emits a valid sale item', () => {
    const emitSpy = spyOn(component.itemPrepared, 'emit');
    component.form.setValue({ productCode: 'P00001', quantity: 2 });

    component.submit();

    expect(emitSpy).toHaveBeenCalledOnceWith({ productCode: 'P00001', quantity: 2 });
  });

  it('does not emit when the quantity is fractional', () => {
    const emitSpy = spyOn(component.itemPrepared, 'emit');
    component.form.setValue({ productCode: 'P00001', quantity: 1.5 });

    component.submit();

    expect(emitSpy).not.toHaveBeenCalled();
    expect(component.form.controls.quantity.touched).toBeTrue();
  });
});
