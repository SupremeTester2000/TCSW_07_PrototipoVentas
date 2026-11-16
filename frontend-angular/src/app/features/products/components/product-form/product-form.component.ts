import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, NonNullableFormBuilder, Validators } from '@angular/forms';
import { ProductRequest } from '../../models/product.model';
import { integerValidator, multipleOfValidator } from '../../../../shared/validators/numeric.validators';

type ProductFormControls = {
  name: FormControl<string>;
  price: FormControl<number>;
  stock: FormControl<number>;
};

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent {
  @Output() readonly productPrepared = new EventEmitter<ProductRequest>();

  readonly form: FormGroup<ProductFormControls>;

  constructor(formBuilder: NonNullableFormBuilder) {
    this.form = formBuilder.group({
      name: ['', [Validators.required, Validators.pattern(/\S/), Validators.maxLength(120)]],
      price: [
        0,
        [
          Validators.required,
          Validators.min(0),
          Validators.max(99999999.99),
          multipleOfValidator(0.01)
        ]
      ],
      stock: [0, [Validators.required, Validators.min(0), integerValidator]]
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.productPrepared.emit(this.form.getRawValue());
    this.form.reset({ name: '', price: 0, stock: 0 });
  }
}
