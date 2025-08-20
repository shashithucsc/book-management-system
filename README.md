# 📚 Book Management System

A simple and modern book management application built with Spring Boot backend and React frontend, using Supabase PostgreSQL database.

## 🚀 Features

- **Book CRUD Operations**: Create, Read, Update, and Delete books
- **User Management**: Register and manage users
- **Modern UI**: Clean, responsive design with beautiful gradients
- **Real-time Database**: PostgreSQL hosted on Supabase
- **RESTful API**: Clean API endpoints for all operations

## 🛠️ Tech Stack

### Backend
- **Spring Boot 3.5.3** - Java framework
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Database (hosted on Supabase)
- **Maven** - Dependency management

### Frontend
- **React 19.1.0** - UI framework
- **Axios** - HTTP client
- **CSS3** - Styling with modern design

### Database
- **Supabase PostgreSQL** - Cloud database

## 📋 Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- Maven
- Supabase account

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd bookapp
```

### 2. Backend Setup

Navigate to the backend directory:
```bash
cd backend
```

The application is configured to use Supabase PostgreSQL. The database connection details are already set in:
- `application-dev.properties` (for development)
- `application-prod.properties` (for production)

### 3. Start the Backend
```bash
# Using Maven
mvn spring-boot:run

# Or using the provided script
./run-backend.ps1
```

The backend will start on `http://localhost:8081`

### 4. Frontend Setup

In a new terminal, navigate to the frontend directory:
```bash
cd frontend
```

Install dependencies:
```bash
npm install
```

### 5. Start the Frontend
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## 📖 API Endpoints

### Books
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Create new book
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## 🗄️ Database Schema

### Books Table
```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    description TEXT,
    isbn VARCHAR(50),
    year INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## 🎨 Features

### Book Management
- Add new books with title, author, description, ISBN, and publication year
- Edit existing books
- Delete books with confirmation
- View all books in a beautiful card layout
- Responsive design for mobile and desktop

### User Management
- User registration with username, password, email, and full name
- User login functionality
- User profile management

## 🔧 Configuration

### Database Configuration
The application uses Supabase PostgreSQL. Update the database connection in:
- `backend/src/main/resources/application-dev.properties`
- `backend/src/main/resources/application-prod.properties`

### Environment Variables
You can override database settings using environment variables:
```bash
export SPRING_DATASOURCE_URL=your_supabase_url
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
```

## 🚀 Deployment

### Backend Deployment
1. Build the JAR file:
```bash
cd backend
mvn clean package
```

2. Run the JAR file:
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
1. Build the production version:
```bash
cd frontend
npm run build
```

2. Deploy the `build` folder to your hosting service.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 🆘 Support

If you encounter any issues or have questions, please create an issue in the repository.

---

**Happy Book Management! 📚✨**
