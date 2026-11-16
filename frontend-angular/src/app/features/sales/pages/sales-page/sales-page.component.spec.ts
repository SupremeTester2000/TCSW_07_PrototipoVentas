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
});
