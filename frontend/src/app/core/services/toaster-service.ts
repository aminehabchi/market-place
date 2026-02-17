import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ToastMessage, ToastType } from '../models/ToastType';


@Injectable({
  providedIn: 'root',
})
export class ToasterService {
  public toaster$ = new BehaviorSubject<ToastMessage | null>(null);

  // Default duration 3000ms
  show(message: string, type: ToastType, duration: number = 3000) {
    const toast = new ToastMessage(type, message); 
    this.toaster$.next(toast);

  }

  success(message: string, duration?: number) {
    this.show(message, 'success', duration);
  }

  error(message: string, duration?: number) {
    this.show(message, 'error', duration);
  }

  info(message: string, duration?: number) {
    this.show(message, 'info', duration);
  }

  warning(message: string, duration?: number) {
    this.show(message, 'warning', duration);
  }
}