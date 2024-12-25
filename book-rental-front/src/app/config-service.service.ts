import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  private config: any;

  constructor(private http: HttpClient) {}

  loadConfig(): Promise<void> {
    return fetch('/assets/config.json')
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Failed to fetch configuration: ${response.statusText}`);
      }
      return response.json();
    })
    .then((config) => {
      console.log('Config loaded:', config);
      this.config = config;
    })
    .catch((error) => {
      console.error('Error loading configuration:', error);
    });
  
  }
  

  get apiBaseUrl(): string {
    return this.config?.apiBaseUrl || '';
  }
}