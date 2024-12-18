import { Component, OnInit, ViewEncapsulation, NgZone, ChangeDetectorRef  } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { AddBookComponent } from '../add-book/add-book.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ApiService } from '../api.service';
import { Book } from '../models/book.model';

@Component({
  selector: 'app-books',
  templateUrl: './books.component.html',
  styleUrls: ['./books.component.css'],
  imports: [CommonModule,AddBookComponent,MatSnackBarModule],
  standalone: true,
  encapsulation: ViewEncapsulation.None,
})
export class BooksComponent implements OnInit {
  books: Book[] = [];
  loading: boolean = true;
  error: boolean = false;

  page: number = 0;
  size: number = 2;
  totalBooks: number = 200;

  showAddBookModal: boolean = false;

  coverLoadingStates: { [bookId: string]: boolean } = {};

  constructor(private apiService: ApiService, private snackBar: MatSnackBar, private ngZone: NgZone, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.loading = true;

    this.apiService
      .get<Book[]>('/v1/books', { size: this.size, page: this.page })
      .subscribe(
        (data) => {
          this.books = data || [];
          this.books.forEach((book) => (this.coverLoadingStates[book.public_id] = true));
          this.loading = false;
          this.error = false;
        },
        (error) => {
          console.error('Error fetching books:', error);
          this.error = true;
          this.loading = false;
        }
      );
  }

  changeAmountFromEvent(bookId: string, event: Event, currentAmount: number): void {
    const inputElement = event.target as HTMLInputElement;
    const targetAmount = parseInt(inputElement.value, 10);

    if (!isNaN(targetAmount) && targetAmount >= 0) {
      this.changeAmount(bookId, targetAmount, currentAmount);
    } else {
      console.error('Invalid amount entered:', inputElement.value);
    }
  }

  changeAmount(bookId: string, targetAmount: number, currentAmount: number): void {
    const delta = targetAmount - currentAmount;
    if (delta === 0) {
      console.log('No change in amount, skipping API call.');
      return;
    }

    this.apiService
      .patch(`/v1/books/${bookId}/amount?value=${delta}`, null)
      .subscribe(
        (response) => {
          console.log('Amount updated successfully:', response);
          this.loadBooks();
        },
        (error) => {
          console.error('Error updating amount:', error);
        }
      );
  }
  
  rentBook(bookId: string, currentAmount: number): void {
    const userPublicId = localStorage.getItem('publicId');

    if (!userPublicId) {
      console.error('User public ID is not set.');
      return;
    }

    if (currentAmount <= 0) {
      this.showSnackBar('Book cannot be rented. No copies available!', 'error-snackbar');
      return;
    }

    this.apiService
      .post(`/v1/rents/book/${bookId}/user/${userPublicId}`, null)
      .subscribe(
        (response) => {
          console.log('Book rented successfully:', response);
          this.changeAmount(bookId, currentAmount - 1, currentAmount);
          this.showSnackBar('Book rented successfully!', 'success-snackbar');
        },
        (error) => {
          console.error('Error renting book:', error);
          this.showSnackBar('Error renting the book. Please try again.', 'error-snackbar');
        }
      );
  }

  private showSnackBar(message: string, panelClass: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: [panelClass],
    });
  }

  toggleAddBookModal(): void {
    this.showAddBookModal = !this.showAddBookModal;
  }

  onBookAdded(): void {
    this.showAddBookModal = false;
    this.loadBooks();
  }

  nextPage(): void {
    if ((this.page + 1) * this.size < this.totalBooks && !this.error) {
      this.page++;
      this.loadBooks();
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadBooks();
    }
  }

  handleImageLoad(bookId: string): void {
    this.coverLoadingStates[bookId] = false;
    this.cdr.detectChanges();
  }

  handleImageError(bookId: string): void {
    this.coverLoadingStates[bookId] = false;
    this.cdr.detectChanges();
  }

  getCoverUrl(isbn: string): string {
    const cleanedIsbn = isbn.replace(/-/g, '');
    return `https://covers.openlibrary.org/b/isbn/${cleanedIsbn}-M.jpg`;
  }
}