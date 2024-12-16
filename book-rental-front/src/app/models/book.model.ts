import { Author } from "./author.model";

export interface Book {
    public_id: string;
    title: string;
    isbn: string;
    amount: number;
    publication_year: number;
    publisher: string;
    authors: Author[];
}