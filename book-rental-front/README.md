# Book Rental Front-End

This is the front-end application for the Book Rental System. Built with Angular, it provides a modern, responsive, and user-friendly interface to manage books, authors, rents, and users. The application also supports server-side rendering (SSR) and uses Angular standalone components.

## Features

- **Book Management**: Add, edit, view, and manage books with pagination, real-time search, and dynamic cover loading.
- **Author Management**: Search and manage authors with an autocomplete search feature.
- **User Management**: Admin functionality to manage user accounts, roles, and statuses.
- **Rent Management**: Rent and return books with user-specific and admin-level views.
- **Dashboard**: Displays key metrics like total books, active rents, and registered users.
- **Search Engine**: Advanced search for books and authors with interactive filtering.
- **Settings**: User-specific settings for updating personal information and passwords.
- **Token Validation**: Automatic session management with periodic token validation.
- **Environment Configuration**: Dynamic base API URL through Angular's environment configuration.

## Technologies Used

- **Framework**: Angular (with standalone components)
- **Styling**: CSS3 (modern styling with flexbox, animations, and shadows)
- **Server-Side Rendering**: Angular Universal
- **Dependency Management**: npm
- **API Communication**: Angular's HttpClient

## Prerequisites
- **Node.js**: Install Node.js version 18 (recommended).
   - Download and install from [Node.js official website](https://nodejs.org/).
   - Verify installation:
     ```bash
     node -v
     npm -v
     ```
- **Angular CLI**: Install Angular CLI globally:
   ```bash
   npm install -g @angular/cli
## Environment Configuration

The project uses a `config.json` file located in `src/assets/config.json` to dynamically configure the API base URL.

Example `config.json`:
```json
{
  "apiBaseUrl": "http://127.0.0.1:8080"
}
```   
## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/xDekann/book-library.git
   cd book-rental-front
2. **Install dependencies:**
   ```bash
   npm install
3. **Configure Environment: Edit the src/assets/config.json file to set the API base URL:**
   ```json
   {
   "apiBaseUrl": "http://127.0.0.1:8080"
   }
4. **Start the Development Server:**
   ```bash
   npm start
5. **Build for Production:**
    ```bash
    npm run build -- --configuration production
## Running in Docker
1. **Build the Docker Image:**
   ```bash
    docker build -t book-rental-frontend .
2. **Run the Docker Container:**
    ```bash
    docker run -p 4200:80 book-rental-frontend
## Known issues
- **Dynamic CSS Loading:**
    - Occasionally, table styles and CSS may load before some logic is implemented, resulting in visible changes to table structure or content alignment as the logic completes.
    - This behavior is temporary and typically resolves within miliseconds as the application completes loading.