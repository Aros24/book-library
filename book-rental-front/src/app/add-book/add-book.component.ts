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
  authorSearchTerm: string = '';
  authorSearchResults: { public_id: string; name: string; birth_year: number }[] = [];

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {  }

  searchAuthors(): void {
    if (this.authorSearchTerm.trim() === '') {
      this.authorSearchResults = [];
      return;
    }

    this.apiService
      .get<{ public_id: string; name: string; birth_year: number }[]>('/v1/authors', {
        name: this.authorSearchTerm,
        size: 5,
        page: 1,
      })
      .subscribe(
        (data) => {
          this.authorSearchResults = data || [];
        },
        (error) => {
          console.error('Error searching authors:', error);
          this.authorSearchResults = [];
        }
      );
  }

  addAuthor(author: { public_id: string; name: string }): void {
    if (!this.selectedAuthors.find((a) => a.public_id === author.public_id)) {
      this.selectedAuthors.push(author);
    }
    this.authorSearchResults = [];
    this.authorSearchTerm = '';
  }

  removeAuthor(author: { public_id: string; name: string }): void {
    this.selectedAuthors = this.selectedAuthors.filter((a) => a.public_id !== author.public_id);
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
