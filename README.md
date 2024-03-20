# :car: Car Sharing Service :car:
Car Sharing is a service for renting cars. API supports different operations:
* Car Management:
    Adding new cars, updating outdated information or deleting no longer available cars.
* Rental Cars Operations:
    Booking a car for a specific duration. Retrieving information about available cars, including model, type, and availability. Checking the rental history for a particular user.
* Notification feature:
    Subcribe on notifications using telegram bot.
* Payment system:
    Finish your rental by paying through Stripe.

## Used Technologies
**Core Technologies:**
* Java 17
* Maven
**Spring Framework:**
* Spring Boot 3.2.0
* Spring Boot Web
* Spring Data JPA
* Spring Boot Security
* Spring Boot Validation
**Database:**
* MySQL 8
* Hibernate
* Liquibase
* H2 for testing
**Testing:**
* Spring Boot Starter Test
* JUnit 5
* Mockito
* Docker Test Containers
**Auxiliary Libraries and tools:**
* Docker
* Lombok
* MapStruct
* Swagger
* JWT
**Thrird party API:**
* Telegram API
* Stripe API
  
## Endpoints
**AuthController:** Handles registration and login requests, supporting both Basic and JWT authentication.
* `POST: /auth/registration` - The endpoint for registration.
* `POST: /auth/login` - The endpoint for login.

**CarController:** Handles requests for car CRUD operations.
* `GET: /cars` - The endpoint for retrieving all books.
* `GET: /cars/{id}` - The endpoint for searching a specific car by ID.
* `POST: /cars` - The endpoint for creating new a car. (Admin Only)
* `PUT: /cars/{id}` - The endpoint for updating car information. (Admin Only)
* `DELETE: /cars/{id}` - The endpoint for deleting car. (Admin Only)

**UserController:** Handles requests for user operations.
* `GET: /users/me` - The endpoint for retrieving your information.
* `PATCH: /users/me` - The endpoint for updating your information.
* `PUT: /users/{id}/role?role=` - The endpoint for updating user's role. (Admin Only)

**RentalController:** Handles requests for rental operations.
* `GET: /rentals/my` - The endpoint for retrieving all rentals by its owner.
* `GET: /rentals?userId=&is_active=` - The endpoint for retrieving all rentals by user and activity. (Admin Only)
* `GET: /rentals/{id}` - The endpoint for retrieving a specific rental by ID. (Admin Only)
* `POST: /rentals` - The endpoint for creating a new rental, it will send notification on creation.
* `POST: /rentals/{id}/return` - The endpoint for returning rental.

**PaymentController:** Handles requests for payment operations.
* `GET: /payments?user_id=` - The endpoint for retrieving all payments by user ID. (Admin Only)
* `GET: /payments/success` - Success endpoint for payment, it will send notification on success.
* `GET: /payments/cancel` - Cancel endpoint for payment.
* `POST: /payments` - The endpoint for creating payment session using Stripe API, it will send notification on creation.

> [!IMPORTANT]
> Also exist scheduled notification (every day at 8am) which inform about overdue rentals.

## Run Project On Your Machine
1. Download [Java](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) and [Docker](https://www.docker.com/products/docker-desktop/).
2. Clone the repository:
    - Open your terminal and paste: `https://github.com/RomanMenzheres/Car-Sharing`
3. Create the .env file with the corresponding variables:
    - You have .env.sample with all necessary variables
4. Build the project:
    - Open project through IDE
    - Enter all needed variables in application.properties or create environment/IDE variables
    - Open your terminal and paste: `mvn clean package`
5. Use Docker Compose:
    - Open your terminal and paste: `docker compose build` and `docker compose up`
  
## Video
