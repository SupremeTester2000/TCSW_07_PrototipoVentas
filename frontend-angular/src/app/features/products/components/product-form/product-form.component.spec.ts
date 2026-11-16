import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { ProductFormComponent } from './product-form.component';

describe('ProductFormComponent', () => {
  let component: ProductFormComponent;
  let fixture: ComponentFixture<ProductFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductFormComponent],
      imports: [ReactiveFormsModule]
    });
    fixture = TestBed.createComponent(ProductFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('does not emit an invalid product and marks the fields as touched', () => {
    const emitSpy = spyOn(component.productPrepared, 'emit');
    component.form.controls.name.setValue('   ');

    component.submit();

    expect(emitSpy).not.toHaveBeenCalled();
    expect(component.form.controls.name.touched).toBeTrue();
    expect(component.form.controls.name.hasError('pattern')).toBeTrue();
  });

  it('emits a product that satisfies the API constraints', () => {
    const emitSpy = spyOn(component.productPrepared, 'emit');
    component.form.setValue({ name: 'Café', price: 12.5, stock: 4 });

    component.submit();

    expect(emitSpy).toHaveBeenCalledOnceWith({ name: 'Café', price: 12.5, stock: 4 });
  });

  it('rejects prices above the contract maximum and fractional stock', () => {
    component.form.setValue({ name: 'Café', price: 100000000, stock: 1.5 });

    expect(component.form.controls.price.hasError('max')).toBeTrue();
    expect(component.form.controls.stock.hasError('integer')).toBeTrue();
  });

  it('rejects prices with more than two decimal places', () => {
    component.form.controls.price.setValue(12.345);

    expect(component.form.controls.price.hasError('multipleOf')).toBeTrue();
  });
});
