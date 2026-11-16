import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StateMessageComponent } from './state-message.component';

describe('StateMessageComponent', () => {
  let component: StateMessageComponent;
  let fixture: ComponentFixture<StateMessageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StateMessageComponent]
    });
    fixture = TestBed.createComponent(StateMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
