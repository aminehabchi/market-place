import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './sub-components/navbar/navbar';
import { ToastComponent } from './sub-components/toast-component/toast-component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
