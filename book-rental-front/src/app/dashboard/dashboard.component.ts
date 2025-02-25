import { Component, ElementRef, HostListener, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterOutlet } from '@angular/router';
import { RouterModule } from '@angular/router';
import { ApiService } from '../api.service';
import { debounceTime, Subject } from 'rxjs';
import { AuthService } from '../auth-service.service';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [RouterOutlet, RouterModule, CommonModule, FormsModule],
  encapsulation: ViewEncapsulation.None,
  standalone: true,
})
export class DashboardComponent {
  constructor(private router: Router, private snackBar: MatSnackBar, private apiService: ApiService, private authService: AuthService, private elementRef: ElementRef) {}

  searchQuery: string = '';
  searchResults: { type: 'book' | 'author'; data: any }[] = [];
  selectedResult: { type: 'book' | 'author'; data: any } | null = null;
  searchInput$ = new Subject<string>();

  userSearchTerm: string = '';
  userSearchResults: any[] = [];
  selectedUser: any | null = null;
  userSearchDebounce: Subject<string> = new Subject<string>();
  

  ngOnInit(): void {
    this.userSearchDebounce.pipe(debounceTime(300)).subscribe((term) => {
      if (term) {
        this.searchUsers(term);
      } else {
        this.userSearchResults = [];
      }
    });
  }

  onUserSearchInputChange(): void {
    this.userSearchDebounce.next(this.userSearchTerm);
  }
  
  searchUsers(term: string): void {
    this.apiService.get<any[]>('/v1/users/accounts', { firstName: term, page: 0, size: 5 }).subscribe(
      (users: any[]) => {
        this.userSearchResults = users;
      },
      (error) => {
        console.error('Error fetching users:', error);
        this.userSearchResults = [];
      }
    );
  }
  
  selectUserForRent(user: any): void {
    this.selectedUser = user;
  }
  
  confirmRentBook(bookId: string): void {
    if (!this.selectedUser) return;
  
    this.apiService.post(`/v1/rents/book/${bookId}/user/${this.selectedUser.public_id}`, {}).subscribe(
      () => {
        this.snackBar.open('Book rented successfully!', 'Close', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });
        this.clearUserSelection();
      },
      (error) => {
        console.error('Error renting book:', error);
        this.snackBar.open('Error renting book. Please try again.', 'Close', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['error-snackbar'],
        });
      }
    );
  }
  
  clearUserSelection(): void {
    this.userSearchTerm = '';
    this.userSearchResults = [];
    this.selectedUser = null;
  }

  isAdmin(): boolean {
    return this.authService.getRole() == 'admin'
  }

  logout(): void {
    localStorage.clear();

    this.snackBar.open('Successfully logged out', 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['success-snackbar'],
    });

    this.router.navigate(['/login']);
  }

  onInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = input.value.trim();
    this.searchQuery = value;
    this.searchInput$.next(value);
  }

  onEnterPress(): void {
    this.performSearch(this.searchQuery);
  }

  performSearch(query: string): void {
    if (query.length === 0) {
      this.searchResults = [];
      return;
    }
  
    const bookTitleSearch = this.apiService.get<any[]>('/v1/books', {
      title: query,
      size: 3,
      page: 1,
    }).toPromise().catch(() => []);
  
    const bookPublisherSearch = this.apiService.get<any[]>('/v1/books', {
      publisher: query,
      size: 3,
      page: 1,
    }).toPromise().catch(() => []);
  
    const bookAuthorSearch = this.apiService.get<any[]>('/v1/books', {
      authorName: query,
      size: 3,
      page: 1,
    }).toPromise().catch(() => []);
  
    const authorSearch = this.apiService.get<any[]>('/v1/authors', {
      name: query,
      size: 4,
      page: 1,
    }).toPromise().catch(() => []);
  
    Promise.all([bookTitleSearch, bookPublisherSearch, bookAuthorSearch, authorSearch])
      .then(([titles, publishers, authorsBooks, authors]) => {
        this.searchResults = [];
  
        if (Array.isArray(titles)) {
          titles.forEach((book) => this.searchResults.push({ type: 'book', data: book }));
        }
  
        if (Array.isArray(publishers)) {
          publishers.forEach((book) => this.searchResults.push({ type: 'book', data: book }));
        }
  
        if (Array.isArray(authorsBooks)) {
          authorsBooks.forEach((book) => this.searchResults.push({ type: 'book', data: book }));
        }
  
        if (Array.isArray(authors)) {
          authors.forEach((author) => this.searchResults.push({ type: 'author', data: author }));
        }
      })
      .catch((error) => {
        console.error('Unexpected error during search:', error);
      });
  }

  viewResultDetails(result: { type: 'book' | 'author'; data: any }): void {
    this.selectedResult = result;
  }

  closeResultDetails(): void {
    this.selectedResult = null;
  }

  getAuthorsList(authors: any[]): string {
    return authors.map((author) => author.name).join(', ');
  }

  clearSearchResults(): void {
    this.searchResults = [];
  }

  formatAuthors(authors: any[]): string {
    return authors.map((author) => author.name).join(', ');
  }

  getCoverUrl(isbn: string): string {
    return `https://covers.openlibrary.org/b/isbn/${isbn}-L.jpg`;
  }

  checkCoverSize(event: Event): void {
    const imgElement = event.target as HTMLImageElement;
    if (imgElement.naturalWidth < 5 && imgElement.naturalHeight < 5) {
      imgElement.src = 'book-cover-placeholder.png';
    }
  }

  rentBook(bookId: string, currentAmount: number): void {
    const userPublicId = localStorage.getItem('publicId');
    if (!userPublicId) {
      console.error('User public ID is not set.');
      return;
    }

    if (currentAmount <= 0) {
      this.snackBar.open('Book cannot be rented. No copies available!', 'Close', {
        duration: 3000,
        panelClass: ['error-snackbar'],
      });
      return;
    }

    this.apiService.post(`/v1/rents/book/${bookId}/user/${userPublicId}`, null).subscribe(
      () => {
        this.snackBar.open('Book rented successfully!', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar'],
        });
      },
      (error) => {
        console.error('Error renting book:', error);
        this.snackBar.open('Error renting the book. Please try again.', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar'],
        });
      }
    );
  }
  
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const targetElement = event.target as HTMLElement;
  
    const clickedInsideSidebar = targetElement.closest('.sidebar');
    const clickedInsideMainContent = targetElement.closest('.main-content');
    const clickedInsideResultSearch = targetElement.closest('.result-details');
  
    if ((clickedInsideSidebar || clickedInsideMainContent) && !clickedInsideResultSearch) {
      this.clearSearchResults();
    }
  }
}
