import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';
import { Rent } from '../models/rent.model';
import { User } from '../models/user.model';
import { Book } from '../models/book.model';

@Component({
  selector: 'app-rent',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rent.component.html',
  styleUrl: './rent.component.css',
  encapsulation: ViewEncapsulation.None,

})
export class RentComponent implements OnInit {
  userRole: string = '';
  userPublicId: string = '';
  rents: Rent[] = [];
  users: User[] = [];
  selectedUser: User | null = null;

  currentPage: number = 0;
  pageSize: number = 5;
  totalUsers: number = 0;

  loading: boolean = true;
  error: boolean = false;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.userRole = localStorage.getItem('userRole') || '';
    this.userPublicId = localStorage.getItem('user_public_id') || '';
    if (this.userRole === 'basic') {
      this.loadUserRents(this.userPublicId);
    } else if (this.userRole === 'admin') {
      this.loadAllUsers();
    }
  }

  loadUserRents(userId: string): void {
    this.loading = true;
    this.apiService.get<Rent[]>(`/v1/rents/user/${userId}`, { page: 0, size: 5 }).subscribe(
      async (rents) => {
        console.log('Rents fetched:', rents);
        this.rents = await Promise.all(
          rents.map(async (rent) => {
            const bookDetails = await this.getBookDetails(rent.book_public_id);
            return {
              ...rent,
              bookDetails,
            };
          })
        );
        console.log('Rents with book details:', this.rents);
        this.loading = false;
      },
      (error) => {
        console.error('Error fetching rents:', error);
        this.error = true;
        this.loading = false;
      }
    );
  }

  private async getBookDetails(bookId: string): Promise<Book | undefined> {
    return this.apiService.get<Book>(`/v1/books/${bookId}`).toPromise();
  }
  
  loadAllUsers(): void {
    this.loading = true;
    this.apiService
      .get<User[]>('/v1/users/accounts', { page: this.currentPage, size: this.pageSize })
      .subscribe(
        (data) => {
          console.log('Users fetched:', data);
          this.users = data || [];
          this.loading = false;
        },
        (error) => {
          console.error('Error fetching users:', error);
          this.error = true;
          this.loading = false;
        }
      );
  }

  endRent(rentId: string): void {
    this.apiService.patch(`/v1/rents/end/${rentId}`).subscribe(
      (response) => {
        console.log('Rent ended successfully:', response);
        this.loadUserRents(this.selectedUser?.public_id || this.userPublicId);
      },
      (error) => {
        console.error('Error ending rent:', error);
      }
    );
  }

  viewUserRents(user: User): void {
    this.selectedUser = user;
    this.loadUserRents(user.public_id);
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadAllUsers();
  }
}