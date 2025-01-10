import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth-service.service';
import { TokenValidationService } from './token-validation.service';
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

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    const isTokenValid = await this.tokenValidator.validateTokenOnDemand();
    const userRole = this.authService.getRole() || '';

    if (this.authService.isTokenValid() && isTokenValid) {
      const allowedRoles = route.data['roles'] as string[];
      if (allowedRoles && !allowedRoles.includes(userRole)) {
        this.router.navigate(['/dashboard']);
        return false;
      }
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}