import { Component, Input } from '@angular/core';
import { ViewState } from '../../models/view-state';

@Component({
  selector: 'app-state-message',
  templateUrl: './state-message.component.html',
  styleUrls: ['./state-message.component.css']
})
export class StateMessageComponent {
  @Input() state: ViewState = 'idle';
  @Input() message = '';
}
