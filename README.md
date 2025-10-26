# ShoeStore - E-commerce Platform

A complete e-commerce website for selling shoes, built with React frontend and Spring Boot backend, featuring a Nike-style design and user experience.

## Features

### User Features
- **Product Browsing**: Browse shoes by categories (Men, Women, Kids)
- **Product Search**: Search products by name, brand, or description
- **Product Details**: Detailed product pages with images, descriptions, and specifications
- **Shopping Cart**: Add/remove items, update quantities
- **Secure Checkout**: Complete checkout process with shipping information
- **Payment Integration**: Razorpay payment gateway integration
- **User Authentication**: Secure login/registration system
- **Order History**: View past orders and their status
- **Responsive Design**: Mobile-friendly interface

### Admin Features
- **Admin Dashboard**: Analytics and overview of store performance
- **Product Management**: Add, edit, delete products
- **Order Management**: View and update order status
- **Inventory Management**: Track stock levels and low stock alerts
- **User Management**: View customer information

## Technology Stack

### Frontend
- **React 18**: Modern React with hooks
- **Redux Toolkit**: State management
- **React Router**: Client-side routing
- **Tailwind CSS**: Utility-first CSS framework
- **React Hook Form**: Form handling and validation
- **Axios**: HTTP client for API calls
- **Lucide React**: Icon library
- **React Hot Toast**: Toast notifications

### Backend
- **Spring Boot 3.2**: Java framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database operations
- **MySQL**: Database
- **JWT**: Token-based authentication
- **Razorpay**: Payment gateway integration
- **Maven**: Dependency management

## Project Structure

```
shoestore/
├── backend/                 # Spring Boot backend
│   ├── src/main/java/com/shoestore/
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── repository/      # Data access layer
│   │   ├── entity/          # JPA entities
│   │   ├── dto/             # Data transfer objects
│   │   ├── security/        # Security configuration
│   │   └── ShoestoreBackendApplication.java
│   ├── src/main/resources/
│   │   └── application.yml  # Configuration
│   └── pom.xml             # Maven dependencies
├── frontend/               # React frontend
│   ├── src/
│   │   ├── components/     # Reusable components
│   │   ├── pages/          # Page components
│   │   ├── store/          # Redux store and slices
│   │   ├── services/       # API services
│   │   └── App.js          # Main app component
│   ├── package.json        # NPM dependencies
│   └── tailwind.config.js  # Tailwind configuration
├── docker-compose.yml      # Docker orchestration
└── README.md              # This file
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Docker (optional)

### Local Development Setup

#### Backend Setup
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd shoestore
   ```

2. Set up MySQL database:
   ```sql
   CREATE DATABASE shoestore;
   CREATE USER 'shoestore'@'localhost' IDENTIFIED BY 'shoestore123';
   GRANT ALL PRIVILEGES ON shoestore.* TO 'shoestore'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. Configure application properties:
   - Update `backend/src/main/resources/application.yml` with your database credentials
   - Add your Razorpay API keys

4. Run the backend:
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

#### Frontend Setup
1. Install dependencies:
   ```bash
   cd frontend
   npm install
   ```

2. Create environment file:
   ```bash
   cp .env.example .env
   ```
   Update the environment variables with your configuration.

3. Run the frontend:
   ```bash
   npm start
   ```

### Docker Setup (Recommended)

1. Clone the repository and navigate to the project directory.

2. Update environment variables in `docker-compose.yml`:
   - Add your Razorpay API keys
   - Update database credentials if needed

3. Run with Docker Compose:
   ```bash
   docker-compose up -d
   ```

This will start:
- MySQL database on port 3306
- Spring Boot backend on port 8080
- React frontend on port 3000

## API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration

### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/gender/{gender}` - Get products by gender
- `GET /api/products/category/{category}` - Get products by category
- `GET /api/products/search?keyword={keyword}` - Search products

### Cart
- `GET /api/cart` - Get cart items
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/{cartItemId}` - Update cart item
- `DELETE /api/cart/{cartItemId}` - Remove cart item
- `DELETE /api/cart/clear` - Clear cart

### Orders
- `GET /api/orders` - Get user orders
- `POST /api/orders` - Create order
- `POST /api/orders/{orderId}/payment` - Create payment order
- `POST /api/orders/{orderId}/verify-payment` - Verify payment

### Admin
- `POST /api/admin/products` - Create product
- `PUT /api/admin/products/{id}` - Update product
- `DELETE /api/admin/products/{id}` - Delete product
- `GET /api/admin/orders` - Get all orders
- `PUT /api/admin/orders/{orderId}/status` - Update order status

## Database Schema

### Users Table
- id, username, email, password, firstName, lastName, phoneNumber, role, createdAt, updatedAt

### Products Table
- id, name, description, price, stockQuantity, brand, category, gender, imageUrls, color, size, material, isActive, createdAt, updatedAt

### Orders Table
- id, userId, totalAmount, status, shippingAddress, city, state, zipCode, country, phoneNumber, paymentId, paymentStatus, orderDate, updatedAt

### Order Items Table
- id, orderId, productId, quantity, price

### Cart Items Table
- id, userId, productId, quantity

## Configuration

### Environment Variables

#### Backend
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: JWT signing secret
- `RAZORPAY_KEY_ID`: Razorpay API key ID
- `RAZORPAY_KEY_SECRET`: Razorpay API key secret

#### Frontend
- `REACT_APP_API_URL`: Backend API URL
- `REACT_APP_RAZORPAY_KEY_ID`: Razorpay API key ID

## Deployment

### Production Deployment

1. **Backend Deployment**:
   - Build the JAR file: `mvn clean package`
   - Deploy to your preferred cloud platform (AWS, Google Cloud, Azure)
   - Configure environment variables
   - Set up MySQL database

2. **Frontend Deployment**:
   - Build the production bundle: `npm run build`
   - Deploy to a static hosting service (Netlify, Vercel, AWS S3)
   - Configure environment variables

3. **Database Setup**:
   - Set up MySQL database in production
   - Run database migrations
   - Configure backup strategies

### Docker Deployment
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Security Features

- JWT-based authentication
- Password encryption with BCrypt
- CORS configuration
- Input validation and sanitization
- SQL injection prevention
- XSS protection
- Secure payment processing with Razorpay

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, email support@shoestore.com or create an issue in the repository.

## Acknowledgments

- Design inspiration from Nike's website
- Icons by Lucide React
- UI components built with Tailwind CSS

