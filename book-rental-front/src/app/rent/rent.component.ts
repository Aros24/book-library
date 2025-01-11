import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';
import { Rent } from '../models/rent.model';
import { User } from '../models/user.model';
import { Book } from '../models/book.model';
import { AuthService } from '../auth-service.service';

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
  showModal: boolean = false;

  currentPage: number = 0;
  yourRentsPage: number = 0; 
  pageSize: number = 5;
  totalUsers: number = 200;
  totalYourRents: number = 200;

  loading: boolean = true;
  error: boolean = false;

  constructor(private apiService: ApiService, private authService: AuthService) {}

  ngOnInit(): void {
    this.userRole = localStorage.getItem('userRole') || '';
    this.userPublicId = localStorage.getItem('publicId') || '';
    this.loadUserRents(this.userPublicId, this.currentPage);
    if (this.userRole === 'admin') {
      this.loadAllUsers();
    }
  }

  loadUserRents(userId: string, page: number): void {
    this.loading = true;
    this.apiService.get<Rent[]>(`/v1/rents/user/${userId}`, { page: page, size: this.pageSize }).subscribe(
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
        this.error = false;
        this.loading = false;
      },
      (error) => {
        if (error.status === 404) {
          this.rents = [];
          this.error = false;
        } else {
          this.error = true;
        }
        this.loading = false;
      }
    );
  }

  changeYourRentsPage(page: number): void {
    if (page < 0) return;
    this.yourRentsPage = page;
    this.loadUserRents(this.userPublicId, page);
  }

  changeAllUsersPage(page: number): void {
    if (page < 0) return;
    this.currentPage = page;
    this.loadAllUsers();
  }

  viewUserRents(user: User): void {
    this.selectedUser = user;
    this.loadUserRents(user.public_id, this.currentPage);
    this.showModal = true;
  }
  
  closeModal(): void {
    this.showModal = false;
    this.selectedUser = null;
  }
  
  backToUserList(): void {
    this.selectedUser = null;
    this.rents = []; 
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

  endRent(rentId: string): void {
    this.apiService.patch(`/v1/rents/end/${rentId}`).subscribe(
      (response) => {
        console.log('Rent ended successfully:', response);
        this.loadUserRents(this.selectedUser?.public_id || this.userPublicId,this.currentPage);
      },
      (error) => {
        console.error('Error ending rent:', error);
      }
    );
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadAllUsers();
  }

  isAdmin(): boolean {
    return this.authService.getRole() == 'admin'
  }

}