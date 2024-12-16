import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-add-book',
  templateUrl: './add-book.component.html',
  styleUrls: ['./add-book.component.css'],
  imports: [FormsModule,CommonModule],
  standalone: true,
})
export class AddBookComponent {
  @Output() bookAdded = new EventEmitter<void>();

  title: string = '';
  isbn: string = '';
  publication_year: number | null = null;
  publisher: string = '';
  selectedAuthors: { public_id: string; name: string }[] = [];
  authors: { public_id: string; name: string; birth_year: number }[] = [];

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadAuthors();
  }

  loadAuthors(): void {
    this.apiService
      .get<{ public_id: string; name: string; birth_year: number }[]>('/v1/authors', {
        page: 1,
        size: 4,
      })
      .subscribe(
        (data) => {
          console.log('Authors loaded:', data);
          this.authors = data;
        },
        (error) => {
          console.error('Error loading authors:', error);
        }
      );
  }

  toggleAuthorSelection(author: { public_id: string; name: string }): void {
    const index = this.selectedAuthors.findIndex(
      (a) => a.public_id === author.public_id
    );
    if (index === -1) {
      this.selectedAuthors.push(author);
    } else {
      this.selectedAuthors.splice(index, 1);
    }
  }
  addBook(): void {
    const payload = {
      title: this.title,
      isbn: this.isbn,
      publication_year: this.publication_year,
      publisher: this.publisher,
      author_public_ids: this.selectedAuthors.map((a) => a.public_id),
    };

    this.apiService.post('/v1/books/add', payload).subscribe(
      (response) => {
        console.log('Book added successfully:', response);
        this.bookAdded.emit();
      },
      (error) => {
        console.error('Error adding book:', error);
      }
    );
  }
}
