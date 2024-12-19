import { Component, ElementRef, HostListener, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterOutlet } from '@angular/router';
import { RouterModule } from '@angular/router';
import { ApiService } from '../api.service';
import { Book } from '../models/book.model';
import { Author } from '../models/author.model';
import { debounceTime, Subject } from 'rxjs';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [RouterOutlet, RouterModule, CommonModule, FormsModule],
  encapsulation: ViewEncapsulation.None,
  standalone: true,
})
export class DashboardComponent {
  constructor(private router: Router, private snackBar: MatSnackBar, private apiService: ApiService, private elementRef: ElementRef) {}

  searchQuery: string = '';
  searchResults: { type: 'book' | 'author'; data: any }[] = [];
  searchInput$ = new Subject<string>();

  ngOnInit(): void {
    this.searchInput$
    .pipe(debounceTime(300))
    .subscribe((query) => this.performSearch(query));
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

  clearSearchResults(): void {
    this.searchResults = [];
  }
  
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const clickedInside = this.elementRef.nativeElement.contains(event.target);
    if (!clickedInside) {
      this.clearSearchResults();
    }
  }
}
