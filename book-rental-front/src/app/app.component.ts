import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TokenValidationService } from './token-validation.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet></router-outlet>`,
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit, OnDestroy {
  constructor(private tokenValidator: TokenValidationService) {}

  ngOnInit(): void {
    this.tokenValidator.startValidation();
  }

  ngOnDestroy(): void {
    this.tokenValidator.stopValidation();
  }
}