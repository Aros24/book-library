import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private tokenExpirationTime: number = 5 * 60 * 1000;
  private tokenRefreshTimeout: any;

  constructor(private router: Router) {}

  saveToken(token: string) {
    localStorage.setItem('token', token);
    this.startTokenExpirationTimer();
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isTokenValid(): boolean {
    const token = this.getToken();
    return !!token;
  }

  startTokenExpirationTimer() {
    if (this.tokenRefreshTimeout) {
      clearTimeout(this.tokenRefreshTimeout);
    }

    this.tokenRefreshTimeout = setTimeout(() => {
      alert('Your session has expired. Please log in again.');
      this.logout();
    }, this.tokenExpirationTime);
  }

  logout() {
    localStorage.removeItem('token');
    clearTimeout(this.tokenRefreshTimeout);
    this.router.navigate(['/login']);
  }
}
