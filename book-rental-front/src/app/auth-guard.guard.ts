import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth-service.service';
import { TokenValidationService } from './token-validation.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router,
    private tokenValidator: TokenValidationService
  ) {}

  async canActivate(): Promise<boolean> {
    const isTokenValid = await this.tokenValidator.validateTokenOnDemand();

    if (this.authService.isTokenValid() && isTokenValid) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}