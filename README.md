# Book-Library
## Summary
Fullstack application for managing a book library. It allows you to create users, add books, book authors and borrow books. Users have two roles possible: basic and admin.
It uses REST API for communication between frontend and backend, with JWT for authentication.

## Technologies Used

### Backend
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)

### Frontend
![Angular](https://img.shields.io/badge/Angular-12-red)

### Prerequisites
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)

### How to run the app
1. **Clone the repository:**
    ```sh
    git clone https://github.com/xDekann/book-library.git
    cd book-library
    ```
2. **Run the app:**
    ```sh
   cd docker
    docker-compose up
     ```
3. **Access the app:**
Open your browser and go to:
    ```sh
    http://localhost:4200
    ```
4. **Stop the app:**
    ```sh
    docker-compose down
    ```

### Accessing the Swagger UI

Once the application is running, you can access the Swagger UI at:
```sh
http://localhost:8080/swagger-ui.html
```

### Tests
Unit tests have been performed on backend using Mockito and JUnit.
Collection on Postman has been created for testing the API.