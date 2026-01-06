# Secret Santa Wishlist Backend

This is the backend REST API for the Secret Santa Wishlist application, built with Spring Boot. 
It allows users to create and manage wishlists, add gifts, and share them with others.

## Tech stack

-   **Java**: 17
-   **Framework**: Spring Boot
-   **Security**: Spring Security + JWT (JSON Web Tokens)
-   **Database**: MySQL (JPA/Hibernate)
-   **Build Tool**: Maven

## Getting Started

### Prerequisites

-   JDK 17 or higher
-   Maven installed (or use the provided `mvnw` wrapper)
-   MySQL Database running

### Installation

1.  Clone the repository.
2.  Configure your database connection in `src/main/resources/application.properties` (Create the file if it doesn't exist):
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/secret_santa_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```
3.  Run the application:
    ```bash
    ./mvnw spring-boot:run
    ```

The application will start on `http://localhost:8080` (default).

## Authentication

The application uses **JWT (JSON Web Token)** for authentication.
-   **Access Token**: Used to access protected resources (Short-lived).
-   **Refresh Token**: Used to obtain a new access token (Long-lived).

### Endpoints

#### Register
**POST** `/api/auth/register`
Creates a new user account.

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123",
  "role": "USER"
}
```

#### Login
**POST** `/api/auth/login`
Authenticates a user and returns tokens.

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "ey...",
  "refreshToken": "ey...",
  "tokenType": "Bearer"
}
```

#### Refresh Token
**POST** `/api/auth/refresh`
Obtains a new access token using a valid refresh token.

**Request Body:**
```json
{
  "refreshToken": "usage-refresh-token-string"
}
```

#### Logout
**POST** `/api/auth/logout`
Invalidates the refresh token (Client should discard tokens).

**Request Body:**
```json
{
  "refreshToken": "usage-refresh-token-string"
}
```

---

## API Endpoints

### Wishlists

#### Get My Wishlists
**GET** `/api/wishlists`
Returns all wishlists created by the authenticated user.

#### Create Wishlist
**POST** `/api/wishlists`

**Request Body:**
```json
{
  "name": "My Christmas List"
}
```

#### Get Wishlist Details
**GET** `/api/wishlists/{wishlistId}`
Returns details of a wishlist.
-   **Owner**: Can view.
-   **Public**: Anyone can view (if wishlist is published).
-   **Private**: Only owner can view.

#### Update Wishlist
**PUT** `/api/wishlists/{wishlistId}`
Updates the wishlist name. (Owner only, if not published).

**Request Body:**
```json
{
  "name": "Updated List Name"
}
```

#### Delete Wishlist
**DELETE** `/api/wishlists/{wishlistId}`
Deletes a wishlist. (Owner only, if not published).

#### Publish Wishlist
**POST** `/api/wishlists/{wishlistId}/publish`
Makes the wishlist public and immutable (cannot add/remove gifts afterwards). (Owner only).

---

### Gifts

#### Get Gift Details
**GET** `/api/wishlists/{wishlistId}/gift/{giftId}`

#### Add Gift
**POST** `/api/wishlists/{wishlistId}/gift`
Adds a gift to a wishlist. (Owner only, if not published).

**Request Body:**
```json
{
  "name": "LEGO Star Wars",
  "imageUrl": "https://example.com/lego.jpg",
  "linkUrl": "https://amazon.com/...",
  "priceInCents": 5000,
  "priority": 5,
  "note": "Specifically this set please!"
}
```

#### Update Gift
**PUT** `/api/wishlists/{wishlistId}/gift/{giftId}`
Updates gift details. (Owner only, if not published).

**Request Body:** (Same as Add Gift)

#### Delete Gift
**DELETE** `/api/wishlists/{wishlistId}/gift/{giftId}`
Removes a gift. (Owner only, if not published).

#### Book Gift
**POST** `/api/wishlists/{wishlistId}/gift/{giftId}/book`
Books a gift on a public wishlist (Secret Santa).

**Request Body:**
```json
{
  "message": "From your Secret Santa!"
}
```