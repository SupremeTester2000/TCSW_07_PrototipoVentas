import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const integerValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value: unknown = control.value;
  if (value === null || value === undefined || value === '') {
    return null;
  }

  return typeof value === 'number' && Number.isInteger(value) ? null : { integer: true };
};

export function multipleOfValidator(divisor: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value: unknown = control.value;
    if (value === null || value === undefined || value === '') {
      return null;
    }
    if (typeof value !== 'number' || !Number.isFinite(value)) {
      return { multipleOf: { divisor } };
    }

    const quotient = value / divisor;
    const isMultiple = Math.abs(quotient - Math.round(quotient)) < 1e-7;
    return isMultiple ? null : { multipleOf: { divisor } };
  };
}
