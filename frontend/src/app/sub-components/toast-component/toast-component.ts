import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-toast',
  imports: [CommonModule],
  templateUrl: './toast-component.html',
})
export class ToastComponent {
  message = signal<string>('hello');
  visible = signal<boolean>(true);
  private timeoutId?: any;

  show(msg: string, duration: number = 3000) {
    this.message.set(msg);
    this.visible.set(true);

    // clear any existing timeout
    if (this.timeoutId) clearTimeout(this.timeoutId);

    // hide toast after duration
    this.timeoutId = setTimeout(() => {
      this.visible.set(false);
    }, duration);
  }
}
