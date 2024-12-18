import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { User } from '../models/user.model';
import { UserToEdit } from '../models/userToEdit.model';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
  standalone: true,
  imports: [CommonModule, MatSnackBarModule,FormsModule],
  encapsulation: ViewEncapsulation.None,
})
export class UsersComponent implements OnInit {
  users: User[] = [];
  loading: boolean = true;
  error: boolean = false;
  editingUser: User | null = null;
  onlyEditUser: UserToEdit = {
    first_name: '',
    last_name: '',
    current_password: '',
    new_password: '',
  };


  page: number = 0;
  size: number = 10;
  totalUsers: number = 500;

  constructor(private apiService: ApiService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.apiService
      .get<User[]>('/v1/users/accounts', { page: this.page, size: this.size })
      .subscribe(
        (data) => {
          this.users = data || [];
          this.error = false;
          this.loading = false;
        },
        (error) => {
          console.error('Error fetching users:', error);
          this.error = true;
          this.loading = false;
        }
      );
  }

  toggleUserStatus(user: User): void {
    const action = user.deleted ? 'enable' : 'disable';
    this.apiService
      .patch(`/v1/users/${user.public_id}/status`)
      .subscribe(
        () => {
          this.showSnackBar(
            `User ${user.first_name} ${user.last_name} has been ${action}d.`,
            'success-snackbar'
          );
          this.loadUsers();
        },
        (error) => {
          console.error(`Error ${action}ing user:`, error);
          this.showSnackBar(
            `Failed to ${action} user. Please try again.`,
            'error-snackbar'
          );
        }
      );
  }

  startEditingUser(user: User): void {
    this.editingUser = { ...user };
    this.onlyEditUser = {
      first_name: user.first_name,
      last_name: user.last_name,
      current_password: '',
      new_password: '',
    };
  }

  saveEditedUser(): void {
    if (!this.onlyEditUser.current_password || !this.onlyEditUser.new_password) {
      this.showSnackBar('Current and New Password are required.', 'error-snackbar');
      return;
    }

    this.apiService
      .patch(`/v1/users/${this.editingUser?.public_id}`, this.onlyEditUser)
      .subscribe(
        () => {
          this.showSnackBar('User updated successfully.', 'success-snackbar');
          this.cancelEditing();
          this.loadUsers();
        },
        (error) => {
          console.error('Error updating user:', error);
          this.showSnackBar('Failed to update user. Please try again.', 'error-snackbar');
        }
      );
  }

  cancelEditing(): void {
    this.editingUser = null;
    this.onlyEditUser = {
      first_name: '',
      last_name: '',
      current_password: '',
      new_password: '',
    };
  }

  private showSnackBar(message: string, panelClass: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: [panelClass],
    });
  }

  nextPage(): void {
    if ((this.page + 1) * this.size < this.totalUsers) {
      this.page++;
      this.loadUsers();
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadUsers();
    }
  }
}
