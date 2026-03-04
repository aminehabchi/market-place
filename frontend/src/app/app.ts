import { Component, Inject, PLATFORM_ID, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './sub-components/navbar/navbar';
import { StateService } from './core/services/state-service';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Navbar],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('frontend');

  constructor(private stateService: StateService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }
  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      let token = localStorage.getItem("token");
      if (token) this.stateService.getMyInfo();
    }
    // this.stateService.getMyInfo();
  }
}
