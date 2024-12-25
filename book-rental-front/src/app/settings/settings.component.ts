import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent implements OnInit {
  settingsForm = {
    first_name: '',
    last_name: '',
    current_password: '',
    new_password: '',
  };

  message: string | null = null;
  success: boolean = false;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    const userId = localStorage.getItem('publicId');
    if (userId) {
      this.apiService.get<any>(`/v1/users/${userId}`).subscribe(
        (user) => {
          this.settingsForm.first_name = user.first_name;
          this.settingsForm.last_name = user.last_name;
        },
        (error) => {
          console.error('Error loading user data:', error);
        }
      );
    }
  }

  updateSettings(): void {
    const userId = localStorage.getItem('publicId');
    if (userId) {
      this.apiService
        .patch(`/v1/users/${userId}`, this.settingsForm)
        .subscribe(
          () => {
            this.message = 'User information updated successfully.';
            this.success = true;
          },
          (error) => {
            console.error('Error updating user settings:', error);
            this.message = 'Failed to update user information. Please try again.';
            this.success = false;
          }
        );
    }
  }

  resetForm(): void {
    this.loadUserData();
    this.settingsForm.current_password = '';
    this.settingsForm.new_password = '';
    this.message = null;
  }
}