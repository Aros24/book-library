CREATE TABLE user (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      public_id VARCHAR(36) NOT NULL UNIQUE,
                      first_name VARCHAR(255) NOT NULL,
                      last_name VARCHAR(255) NOT NULL,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      role VARCHAR(50) DEFAULT 'basic',
                      deleted TINYINT(1) DEFAULT 0 NOT NULL,
                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE book (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      public_id VARCHAR(36) NOT NULL UNIQUE,
                      title VARCHAR(255) NOT NULL,
                      isbn VARCHAR(13) NOT NULL UNIQUE,
                      publication_year INT,
                      publisher VARCHAR(255),
                      created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE author (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        public_id VARCHAR(36) NOT NULL UNIQUE,
                        name VARCHAR(255) NOT NULL,
                        birth_year INT
);

CREATE TABLE rent (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      public_id VARCHAR(36) NOT NULL UNIQUE,
                      user_id INT NOT NULL,
                      user_public_id VARCHAR(36) NOT NULL,
                      book_id INT NOT NULL,
                      book_public_id VARCHAR(36) NOT NULL,
                      start_date DATETIME NOT NULL,
                      end_date DATETIME,
                      FOREIGN KEY (user_id) REFERENCES user(id),
                      FOREIGN KEY (book_id) REFERENCES book(id)
);

CREATE TABLE book_author (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             public_id VARCHAR(36) NOT NULL UNIQUE,
                             book_id INT NOT NULL,
                             book_public_id VARCHAR(36) NOT NULL,
                             author_id INT NOT NULL,
                             author_public_id VARCHAR(36) NOT NULL,
                             FOREIGN KEY (book_id) REFERENCES book(id),
                             FOREIGN KEY (author_id) REFERENCES author(id)
);

-- Create Indexes
CREATE INDEX idx_user_public_id ON user(public_id);
CREATE INDEX idx_user_email ON user(email);

CREATE INDEX idx_book_public_id ON book(public_id);
CREATE INDEX idx_book_isbn ON book(isbn);
CREATE INDEX idx_book_publisher ON book(publisher);

CREATE INDEX idx_author_public_id ON author(public_id);
CREATE INDEX idx_author_name ON author(name);

CREATE INDEX idx_rent_public_id ON rent(public_id);
CREATE INDEX idx_rent_user_id ON rent(user_id);
CREATE INDEX idx_rent_user_public_id ON rent(user_public_id);
CREATE INDEX idx_rent_book_id ON rent(book_id);
CREATE INDEX idx_rent_book_public_id ON rent(book_public_id);

CREATE INDEX idx_book_author_public_id ON book_author(public_id);
CREATE INDEX idx_book_author_book_id ON book_author(book_id);
CREATE INDEX idx_book_author_book_public_id ON book_author(book_public_id);
CREATE INDEX idx_book_author_author_id ON book_author(author_id);
CREATE INDEX idx_book_author_author_public_id ON book_author(author_public_id);

INSERT INTO user (public_id, first_name, last_name, email, password, role, deleted)
VALUES
    ('123e4567-e89b-12d3-a456-426614174001', 'User', 'One', 'user1@example.com', '$2b$12$mGLhDK.9eb49r5dUjJHC.ekEHIAMKvyLsQrZCJBsYZvEOm.VSYZzK', 'basic', 0),

    ('123e4567-e89b-12d3-a456-426614174002', 'Admin', 'One', 'admin1@example.com', '$2a$12$U9B1BLDyDEXksGRa5u3Qk.5uKGICs.xER.qRdmHGKHe/gNWybfiqi', 'admin', 0),

    ('123e4567-e89b-12d3-a456-426614174003', 'Admin', 'Two', 'admin2@example.com', '$2a$12$TqjGpwwNCU.w0mb8BUVWW.s7yPvrFxnCsBX0r1eFmPVNSfn90sgLW', 'admin', 0),

    ('123e4567-e89b-12d3-a456-426614174004', 'User', 'Two', 'user2@example.com', '$2a$12$8PkAUnlowjw./a.Eqizki.TlzjS8zqf9KJHy8XGiF7ZfRnMkHQwd2', 'basic', 0),

    ('123e4567-e89b-12d3-a456-426614174005', 'User', 'Three', 'user3@example.com', '$2a$12$NVXudWs5ggwXqc8FojRXWeZjaLJRmpcZ1Vq4BaBGYZXkAoxCRtQDq', 'basic', 0);
