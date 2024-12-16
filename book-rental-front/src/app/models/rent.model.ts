export interface Rent {
    public_id: string;
    book_title: string;
    book_authors: string[];
    book_public_id: string;
    start_date: string;
    end_date: string | null;
    user_public_id: string;
}