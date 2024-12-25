import { Injectable } from '@angular/core';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private tokenKey = 'authToken';
  private roleKey = 'userRole';
  private publicId = 'publicId';

  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  saveRole(role: string): void {
    localStorage.setItem(this.roleKey, role);
  }

  getRole(): string | null {
    return localStorage.getItem(this.roleKey);
  }

  savePublicUser(publicUser: string): void {
    localStorage.setItem(this.publicId, publicUser);
  }

  getPublicUser(): string | null {
    return localStorage.getItem(this.publicId);
  }


  isTokenValid(): boolean {
    const token = this.getToken();
    return token !== null && token !== '';
  }

  clearStorage(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.publicId);
  }
}
