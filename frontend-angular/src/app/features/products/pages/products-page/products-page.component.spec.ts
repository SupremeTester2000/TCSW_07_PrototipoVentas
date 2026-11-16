import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';

import { ProductsPageComponent } from './products-page.component';
import { ProductFormComponent } from '../../components/product-form/product-form.component';
import { ProductListComponent } from '../../components/product-list/product-list.component';
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
        StateMessageComponent
      ],
      imports: [ReactiveFormsModule]
    });
    fixture = TestBed.createComponent(ProductsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
