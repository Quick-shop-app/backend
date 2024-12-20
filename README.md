# Quick Shop Admin Dashboard

Quick Shop is an e-commerce application designed for managing an online store. It provides functionalities for user authentication, product management, and shopping cart operations.
This application includes server-side rendered (SSR) views and a REST API for every feature. 

---

## Features

### Admin Dashboard
- View, create, update, and delete products.
- Soft delete functionality for products (inactive products are hidden from public view).
- Manage orders and export database content as SQL.

### REST API
- Endpoints for product cart and order management.
- Basic Authentication for secure access.
- Supports file uploads for product images.

### Soft Delete Support
- Products can be marked as inactive without permanent deletion.

### File Management
- File uploads using `MultipartFile`, stored in the `/public/images` directory.

---

## Technology Stack

### Backend: Spring Boot, Spring Data JPA, Spring Security.
- **Spring Boot 3.3.4**
- **Spring Security** (Basic Authentication)
- **Spring Data JPA** (Hibernate)
  
### Frontend: Thymeleaf, Bootstrap.
- **Bootstrap 5** for styling Thymeleaf templates.
  
### Database: MySQL

## Getting Started

### Prerequisites
- **Java**: 21 or later
- **Maven**: 3.8 or later
- **MySQL Database**

### Installation

#### Backend Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/username/quickshop.git
   cd quickshop
   ```

2. Configure the `application.properties` file in `src/main/resources`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/quickshop
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   spring.h2.console.enabled=true
   spring.security.user.name=admin
   spring.security.user.password=admin
   ```

3. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Documentation

### Authentication
- **POST /api/auth/register**: Register a new user.
 
- **POST /api/auth/login**: Log in a user.

### Products
- **GET /api/products**: Fetch all available products (public access).

### Cart
- **POST /api/cart/add?productId={id}&quantity={qty}**: Add a product to the cart (authenticated users).
- **POST /api/cart/remove?productId={id}**: Remove a product from the cart (authenticated users).
- **POST /api/cart/clear**: Clear all cart items (authenticated users).
- **GET /api/cart**: View cart contents (authenticated users).
- **POST /api/cart/finalize**: Finalize the purchase (authenticated users).

### Admin
- **GET /api/admin/products**: View all products (admins only).
- **POST /api/admin/products**: Create a new product (admins only).
- **PUT /api/admin/products/{id}**: Edit a product (admins only).
- **DELETE /api/admin/products/{id}**: Delete a product (admins only).
- **GET /api/admin/products/download-db**: Export the database (admins only).

### Access Restrictions

- **Public Access**: Viewing products, login, and register.
- **Authenticated Users Only**: Accessing and managing the cart, viewing order history.
- **Admins Only**: Performing CRUD operations on products and exporting the database.

## Postman Documentation

For detailed API usage and examples, refer to the Postman documentation: [QuickShop Postman API Documentation](https://documenter.getpostman.com/view/17235107/2sAYJ3ELxC).

## Deployment

QuickShop has been successfully deployed using Docker, AWS EC2, NGINX, and secured with Let's Encrypt. Visit the live application at [QuickShop](https://www.quick-shop.tech/).


