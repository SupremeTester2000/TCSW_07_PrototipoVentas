import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, NonNullableFormBuilder, Validators } from '@angular/forms';
import { integerValidator } from '../../../../shared/validators/numeric.validators';
import { SaleItem } from '../../models/sale.model';

type SaleFormControls = {
  productCode: FormControl<string>;
  quantity: FormControl<number>;
};

@Component({
  selector: 'app-sale-form',
  templateUrl: './sale-form.component.html',
  styleUrls: ['./sale-form.component.css']
})
export class SaleFormComponent {
  @Output() readonly itemPrepared = new EventEmitter<SaleItem>();

  readonly form: FormGroup<SaleFormControls>;

  constructor(formBuilder: NonNullableFormBuilder) {
    this.form = formBuilder.group({
      productCode: ['', [Validators.required, Validators.pattern(/^P[0-9]{5}$/)]],
      quantity: [1, [Validators.required, Validators.min(1), integerValidator]]
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.itemPrepared.emit(this.form.getRawValue());
    this.form.reset({ productCode: '', quantity: 1 });
  }
}
