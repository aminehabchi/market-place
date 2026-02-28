import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './sub-components/navbar/navbar';
import { StateService } from './core/services/state-service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Navbar],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('frontend');

  constructor(private stateService: StateService) { }
  ngOnInit() {
    this.stateService.getMyInfo()
  }

}