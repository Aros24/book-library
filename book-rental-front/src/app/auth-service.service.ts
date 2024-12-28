import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private tokenKey = 'authToken';
  private roleKey = 'userRole';
  private publicId = 'publicId';
  private isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  saveToken(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem(this.tokenKey, token);
    }
  }

  getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem(this.tokenKey);
    }
    return null;
  }

  saveRole(role: string): void {
    if (this.isBrowser) {
      localStorage.setItem(this.roleKey, role);
    }
  }

  getRole(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem(this.roleKey);
    }
    return null;
  }

  savePublicUser(publicUser: string): void {
    if (this.isBrowser) {
      localStorage.setItem(this.publicId, publicUser);
    }
  }

  getPublicUser(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem(this.publicId);
    }
    return null;
  }

  isTokenValid(): boolean {
    const token = this.getToken();
    return token !== null && token !== '';
  }

  clearStorage(): void {
    if (this.isBrowser) {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.roleKey);
      localStorage.removeItem(this.publicId);
    }
  }
}
