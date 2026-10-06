# Wanderlust -- Spring Boot Backend

A secure REST API backend for **Wanderlust**, a full-stack property
discovery and booking platform.

The backend is built with **Java, Spring Boot, Spring Security, JWT,
Spring Data JPA/Hibernate, MySQL, Cloudinary, Swagger/OpenAPI, JUnit,
and Mockito**.

It provides authentication, property management, image uploads,
search/filter/sort, bookings, reviews, host profiles, authorization,
validation, exception handling, and testing support.

------------------------------------------------------------------------

## 1. Project Overview

Wanderlust allows users to:

-   Register and log in securely.
-   Browse properties without authentication.
-   Search by location.
-   Filter by price and guest capacity.
-   Sort properties by price.
-   View property details and images.
-   Create property listings after authentication.
-   Upload one or multiple property images.
-   Update/delete authorized properties.
-   Book properties for selected dates.
-   Prevent overlapping bookings.
-   Cancel bookings.
-   View booking history.
-   Review properties after a confirmed booking.
-   Update/delete reviews.
-   View host profiles.
-   Hosts can view bookings for their properties.
-   Hosts can cancel bookings for their properties.
-   Hosts can update their profile and upload a profile photo.

The backend independently enforces authentication and authorization, so
hiding a frontend button is not the security mechanism.

------------------------------------------------------------------------

## 2. Technology Stack

  Technology           Purpose
  -------------------- ----------------------------------
  Java 21              Backend language
  Spring Boot 4.x      Application framework
  Spring Web           REST APIs
  Spring Security      Authentication and authorization
  JWT                  Stateless authentication
  BCrypt               Password hashing
  Spring Data JPA      Database access
  Hibernate            ORM
  MySQL                Relational database
  Jakarta Validation   Request validation
  Cloudinary           Image storage
  Maven                Build/dependency management
  Swagger / OpenAPI    API documentation
  JUnit 5              Unit testing
  Mockito              Mocking
  Postman              API testing
  Git / GitHub         Version control

------------------------------------------------------------------------

## 3. Architecture

The project follows a layered architecture:

``` text
Client / React
      |
      | HTTP / REST
      v
Controller
      |
      v
Service
      |
      v
Repository
      |
      v
JPA / Hibernate
      |
      v
MySQL
```

External services:

``` text
Spring Boot
   |
   +---- MySQL
   |
   +---- Cloudinary
   |
   +---- JWT Security
```

### Controller Layer

Handles:

-   HTTP requests/responses
-   Path variables
-   Query parameters
-   Request bodies
-   Multipart uploads
-   API documentation

Main controllers:

``` text
AuthController
PropertyController
BookingController
ReviewController
HostProfileController
```

### Service Layer

Contains business logic:

``` text
AuthService
PropertyService
BookingService
ReviewService
HostProfileService
CloudinaryService
```

### Repository Layer

Database access through Spring Data JPA:

``` text
UserRepository
PropertyRepository
PropertyImageRepository
BookingRepository
ReviewRepository
```

------------------------------------------------------------------------

# 4. Authentication and JWT Flow

Wanderlust uses stateless JWT authentication.

## Login

``` text
React Login
    |
    | POST /api/auth/login
    v
AuthController
    |
    v
AuthService
    |
    +--> Find user by email
    |
    +--> BCrypt password verification
    |
    v
JwtService
    |
    v
JWT Token
    |
    v
React stores token
```

## Protected Request

``` text
React
    |
    | Authorization: Bearer <JWT>
    v
JwtAuthenticationFilter
    |
    +--> Validate JWT
    +--> Extract user/email
    +--> Load user
    |
    v
SecurityContext
    |
    v
Controller
    |
    v
Service
```

Example header:

``` http
Authorization: Bearer <JWT_TOKEN>
```

------------------------------------------------------------------------

# 5. Roles and Authorization

Supported roles:

``` text
USER
ADMIN
```

### USER

A normal authenticated user can:

-   Create properties.
-   Manage their own properties.
-   Upload images for their properties.
-   Create bookings.
-   View their bookings.
-   Cancel their bookings.
-   Create reviews after a confirmed booking.
-   Update/delete their reviews.
-   Update their host profile.
-   Upload a profile photo.

### ADMIN

Administrators have broader management permissions where supported by
the service layer.

Ownership checks are still applied to normal users.

------------------------------------------------------------------------

# 6. Important Security Rule -- Property Creation

Only authenticated users can create properties.

Spring Security protects:

``` http
POST /api/properties
```

The owner is taken from the authenticated user rather than from an
`ownerId` supplied by the frontend.

``` java
User owner = getCurrentUser();
property.setOwner(owner);
```

Flow:

``` text
Unauthenticated
      |
      v
POST /api/properties
      |
      v
Spring Security
      |
      v
401 Unauthorized
      |
      X
No property created
```

Authenticated:

``` text
Login
  |
  v
JWT
  |
  v
POST /api/properties
  |
  v
Authenticated User
  |
  v
Property owner = current user
  |
  v
Property created
```

------------------------------------------------------------------------

# 7. Property Management

A property contains:

-   Title
-   Description
-   Location
-   Price per night
-   Maximum guests
-   Owner
-   Image URL(s)

Features:

-   Create property
-   Get all properties
-   Get property by ID
-   Get current user's properties
-   Update property
-   Delete property
-   Upload one image
-   Upload multiple images
-   Delete property image
-   Search
-   Filter
-   Sort

------------------------------------------------------------------------

# 8. Property Search / Filter / Sort

### Location

``` http
GET /api/properties?location=Pune
```

### Minimum price

``` http
GET /api/properties?minPrice=2000
```

### Maximum price

``` http
GET /api/properties?maxPrice=5000
```

### Guests

``` http
GET /api/properties?guests=4
```

### Price ascending

``` http
GET /api/properties?sort=priceAsc
```

### Combined

``` http
GET /api/properties?location=Pune&minPrice=2000&maxPrice=5000&guests=4&sort=priceAsc
```

Filtering uses Spring Data JPA specifications.

------------------------------------------------------------------------

# 9. Property Image Architecture

Cloudinary stores the actual images while MySQL stores image URLs.

``` text
React
  |
  | multipart/form-data
  v
PropertyController
  |
  v
PropertyService
  |
  v
CloudinaryService
  |
  v
Cloudinary
  |
  | secure_url
  v
PropertyImage
  |
  v
MySQL
```

Multipart limits can be configured with:

``` properties
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB
```

------------------------------------------------------------------------

# 10. Booking System

A booking contains:

``` text
User
Property
Check-in
Check-out
Guests
Total Price
Status
```

Statuses include:

``` text
CONFIRMED
CANCELLED
```

All booking APIs require authentication.

## Booking validation

Before creating a booking:

1.  Property must exist.
2.  Check-out must be after check-in.
3.  Guests cannot exceed property capacity.
4.  Overlapping non-cancelled bookings are rejected.
5.  Total price is calculated by the backend.
6.  New bookings are created as `CONFIRMED`.

Cancelled bookings do not block availability.

## Price calculation

``` text
Number of nights = Check-out - Check-in

Total price = Price per night × Number of nights
```

The backend calculates the price instead of trusting a frontend-provided
total.

------------------------------------------------------------------------

# 11. Booking Data Flow

``` text
React
 |
 | POST /api/bookings
 v
BookingController
 |
 v
BookingService
 |
 +--> Current authenticated user
 +--> Find property
 +--> Validate dates
 +--> Validate guest capacity
 +--> Check overlapping bookings
 +--> Calculate total price
 +--> Set CONFIRMED
 |
 v
BookingRepository
 |
 v
MySQL
 |
 v
BookingResponse
 |
 v
React
```

------------------------------------------------------------------------

# 12. Review System

Users can review a property only after a confirmed booking.

Rules:

-   Authentication required.
-   Rating must be between 1 and 5.
-   A confirmed booking is required.
-   Duplicate reviews for the same user/property are prevented.
-   Users can update/delete their reviews according to authorization.

Flow:

``` text
User
 |
 | Create review
 v
ReviewService
 |
 +--> Find property
 +--> Find current user
 +--> Check confirmed booking
 +--> Check duplicate review
 |
 v
ReviewRepository
 |
 v
MySQL
```

------------------------------------------------------------------------

# 13. Host Profile

Host profile data includes:

-   Host ID
-   Name
-   Email
-   Phone
-   Profile image
-   About
-   Hosting date
-   Total properties
-   Total reviews
-   Average rating
-   Host reviews
-   Host properties

Hosts can:

-   Update name
-   Update phone
-   Update about
-   Upload profile photo
-   View bookings for their properties

------------------------------------------------------------------------

# 14. Standard API Response

Most APIs use a common response wrapper:

``` json
{
  "success": true,
  "message": "Property fetched successfully",
  "data": {}
}
```

This gives the frontend a consistent response format.

------------------------------------------------------------------------

# 15. Complete API Reference

Base URL:

``` text
http://localhost:8080/api
```

## Authentication

  Method   Endpoint           Auth
  -------- ------------------ --------
  POST     `/auth/register`   Public
  POST     `/auth/login`      Public

### Register

``` http
POST /api/auth/register
```

Example:

``` json
{
  "name": "Shyam Lade",
  "email": "shyam@example.com",
  "phone": "9876543210",
  "about": "Software developer",
  "password": "password123"
}
```

### Login

``` http
POST /api/auth/login
```

Example:

``` json
{
  "email": "shyam@example.com",
  "password": "password123"
}
```

Returns user information and JWT token.

------------------------------------------------------------------------

# Property APIs

  Method   Endpoint                                      Auth
  -------- --------------------------------------------- ----------
  GET      `/properties`                                 Public
  GET      `/properties/{id}`                            Public
  GET      `/properties/my`                              Required
  POST     `/properties`                                 Required
  PUT      `/properties/{id}`                            Required
  DELETE   `/properties/{id}`                            Required
  POST     `/properties/{id}/image`                      Required
  POST     `/properties/{id}/images`                     Required
  DELETE   `/properties/{propertyId}/images/{imageId}`   Required

### Create property

``` http
POST /api/properties
```

``` json
{
  "title": "Mountain View Villa",
  "description": "A beautiful villa surrounded by nature.",
  "location": "Pune",
  "pricePerNight": 3500,
  "maxGuests": 4
}
```

### Get all properties

``` http
GET /api/properties
```

### Get current user's properties

``` http
GET /api/properties/my
```

### Get property

``` http
GET /api/properties/{id}
```

### Update property

``` http
PUT /api/properties/{id}
```

### Delete property

``` http
DELETE /api/properties/{id}
```

### Upload one image

``` http
POST /api/properties/{id}/image
```

Multipart field:

``` text
file
```

### Upload multiple images

``` http
POST /api/properties/{id}/images
```

Multipart field:

``` text
files
```

### Delete image

``` http
DELETE /api/properties/{propertyId}/images/{imageId}
```

------------------------------------------------------------------------

# Booking APIs

  Method   Endpoint                       Auth
  -------- ------------------------------ ----------
  POST     `/bookings`                    Required
  GET      `/bookings/my`                 Required
  GET      `/bookings/{id}`               Required
  PATCH    `/bookings/{id}/cancel`        Required
  GET      `/bookings/host`               Required
  PATCH    `/bookings/{id}/host-cancel`   Required
  DELETE   `/bookings/{id}`               Required

### Create booking

``` http
POST /api/bookings
```

``` json
{
  "propertyId": 1,
  "checkIn": "2026-10-15",
  "checkOut": "2026-10-18",
  "guests": 2
}
```

### My bookings

``` http
GET /api/bookings/my
```

### Booking details

``` http
GET /api/bookings/{id}
```

### Cancel booking

``` http
PATCH /api/bookings/{id}/cancel
```

### Host bookings

``` http
GET /api/bookings/host
```

### Host cancels booking

``` http
PATCH /api/bookings/{id}/host-cancel
```

### Delete booking

``` http
DELETE /api/bookings/{id}
```

The service verifies whether the authenticated user is authorized to
delete the booking.

------------------------------------------------------------------------

# Review APIs

  Method   Endpoint                                        Auth
  -------- ----------------------------------------------- ----------
  GET      `/properties/{propertyId}/reviews`              Public
  POST     `/properties/{propertyId}/reviews`              Required
  PUT      `/properties/{propertyId}/reviews/{reviewId}`   Required
  DELETE   `/properties/{propertyId}/reviews/{reviewId}`   Required

### Get reviews

``` http
GET /api/properties/{propertyId}/reviews
```

### Create review

``` http
POST /api/properties/{propertyId}/reviews
```

``` json
{
  "rating": 5,
  "comment": "Excellent property and great experience."
}
```

### Update review

``` http
PUT /api/properties/{propertyId}/reviews/{reviewId}
```

### Delete review

``` http
DELETE /api/properties/{propertyId}/reviews/{reviewId}
```

------------------------------------------------------------------------

# Host Profile APIs

  Method   Endpoint                 Auth
  -------- ------------------------ ----------
  GET      `/hosts/{hostId}`        Public
  PUT      `/hosts/profile`         Required
  POST     `/hosts/profile/photo`   Required

### Get host profile

``` http
GET /api/hosts/{hostId}
```

### Update host profile

``` http
PUT /api/hosts/profile
```

``` json
{
  "name": "Shyam Lade",
  "phone": "9876543210",
  "about": "Java Full Stack Developer"
}
```

### Upload profile photo

``` http
POST /api/hosts/profile/photo
```

Multipart field:

``` text
file
```

------------------------------------------------------------------------

# 16. Database Model

Main entities:

``` text
User
Property
PropertyImage
Booking
Review
```

Relationships:

``` text
USER
 |
 +---- 1:N ---- PROPERTY
 |
 +---- 1:N ---- BOOKING
 |
 +---- 1:N ---- REVIEW

PROPERTY
 |
 +---- 1:N ---- PROPERTY_IMAGE
 |
 +---- 1:N ---- BOOKING
 |
 +---- 1:N ---- REVIEW
```

Conceptually:

``` text
User
  |
  | owns
  v
Property
  |
  +---- PropertyImage
  |
  +---- Booking
  |
  +---- Review
```

A booking belongs to both a user and a property.

A review belongs to both a user and a property.

------------------------------------------------------------------------

# 17. Security Configuration

Spring Security provides:

-   Stateless sessions
-   JWT authentication
-   Password encoding
-   Endpoint authorization
-   CORS configuration
-   Authentication filter

Public endpoints include:

``` text
POST /api/auth/register
POST /api/auth/login

GET /api/properties/**
GET /api/properties/{propertyId}/reviews
GET /api/hosts/**
```

Protected operations include:

``` text
POST /api/properties
PUT /api/properties/**
DELETE /api/properties/**

POST /api/bookings
GET /api/bookings/**
PATCH /api/bookings/**
DELETE /api/bookings/**

POST /api/properties/*/reviews
PUT /api/properties/*/reviews/*
DELETE /api/properties/*/reviews/*

PUT /api/hosts/profile
POST /api/hosts/profile/photo
```

CORS allows the React frontend to communicate with the API during local
development.

------------------------------------------------------------------------

# 18. Password Security

Passwords are never stored as plain text.

``` text
Raw Password
      |
      v
BCrypt PasswordEncoder
      |
      v
Hashed Password
      |
      v
MySQL
```

During login:

``` text
Entered Password
      |
      v
BCrypt.matches()
      |
      +---- Match ----> Generate JWT
      |
      +---- Fail -----> Reject login
```

------------------------------------------------------------------------

# 19. Validation and Exception Handling

Jakarta Validation is used for request validation.

Examples:

``` text
@NotBlank
@Email
@Size
@Min
@Max
```

The application also uses centralized exception handling for conditions
such as:

``` text
Resource not found
Invalid request
Validation failure
Unauthorized request
Access denied
Duplicate resource
Invalid booking dates
Booking conflict
Invalid image
Upload size exceeded
Property deletion restrictions
```

Typical HTTP statuses:

``` text
200 OK
201 CREATED
400 BAD REQUEST
401 UNAUTHORIZED
403 FORBIDDEN
404 NOT FOUND
409 CONFLICT
500 INTERNAL SERVER ERROR
```

------------------------------------------------------------------------

# 20. Property Deletion Protection

Properties with existing bookings are protected from deletion according
to the application's business rule.

The backend checks whether bookings exist before allowing property
deletion.

This prevents orphaned or inconsistent booking records.

------------------------------------------------------------------------

# 21. Swagger / OpenAPI

Swagger/OpenAPI is integrated for interactive API documentation.

When running locally:

``` text
http://localhost:8080/swagger-ui/index.html
```

Swagger provides:

-   Endpoint documentation
-   Request models
-   Response models
-   Query parameters
-   Authentication support
-   Try-it-out functionality

------------------------------------------------------------------------

# 22. Project Structure

``` text
src/
├── main/
│   ├── java/
│   │   └── com/wanderlust/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       ├── service/
│   │       └── specification/
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com/wanderlust/
```

------------------------------------------------------------------------

# 23. Configuration

Example local configuration:

``` properties
server.port=8080
server.address=0.0.0.0

spring.datasource.url=jdbc:mysql://localhost:3306/wanderlust
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB

# JWT
# jwt.secret=YOUR_SECRET

# Cloudinary
# cloudinary.cloud-name=YOUR_CLOUD_NAME
# cloudinary.api-key=YOUR_API_KEY
# cloudinary.api-secret=YOUR_API_SECRET
```

**Never commit real passwords, JWT secrets, API keys, or Cloudinary
secrets to GitHub.**

Use environment variables or an ignored local configuration file.

------------------------------------------------------------------------

# 24. Running the Backend

## Prerequisites

Install:

-   Java 21
-   Maven
-   MySQL
-   Git

Optional:

-   Postman
-   IntelliJ IDEA / Eclipse

## Create database

``` sql
CREATE DATABASE wanderlust;
```

## Clone

``` bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd <PROJECT_FOLDER>
```

## Run

``` bash
mvn spring-boot:run
```

Or run the Spring Boot main class from the IDE.

Backend:

``` text
http://localhost:8080
```

Swagger:

``` text
http://localhost:8080/swagger-ui/index.html
```

------------------------------------------------------------------------

# 25. Postman Testing Flow

Recommended order:

``` text
1. Register
2. Login
3. Copy JWT
4. Add Bearer token
5. Create property
6. Upload property images
7. Get properties
8. Create booking
9. View booking
10. Cancel booking
11. Create review
12. Update review
13. Delete review
14. Test host profile
```

Protected APIs require:

``` http
Authorization: Bearer <JWT_TOKEN>
```

------------------------------------------------------------------------

# 26. Security Test Cases

Important scenarios:

### Unauthenticated property creation

``` text
POST /api/properties
without JWT
       |
       v
401 Unauthorized
```

### Authenticated property creation

``` text
POST /api/properties
with valid JWT
       |
       v
201 Created
```

### Unauthorized property update

A normal user cannot update another user's property.

### Unauthorized image upload

A normal user cannot upload images to another user's property.

### Review without booking

A user without a confirmed booking cannot create a review.

### Duplicate review

A user cannot create multiple reviews for the same property when the
business rule prevents duplicates.

### Booking overlap

Overlapping non-cancelled bookings are rejected.

------------------------------------------------------------------------

# 27. Mobile / Local Network Testing

The backend can be tested from a phone when the phone and development PC
are on the same Wi-Fi or mobile hotspot.

Example:

``` text
PC IP:
10.x.x.x
```

React:

``` text
http://10.x.x.x:5173
```

Spring Boot:

``` text
http://10.x.x.x:8080
```

Vite:

``` bash
npm run dev -- --host 0.0.0.0
```

Spring Boot:

``` properties
server.address=0.0.0.0
server.port=8080
```

CORS must allow the current React origin.

The local IP can change when switching Wi-Fi networks or reconnecting a
mobile hotspot.

------------------------------------------------------------------------

# 28. Testing

The project uses:

``` text
JUnit 5
Mockito
```

Testing is focused on service/business logic and repository behavior.

Areas tested include:

-   Booking service
-   Review service
-   Repository methods
-   Booking validation
-   Review eligibility
-   Authorization/business rules

------------------------------------------------------------------------

# 29. End-to-End Data Flow

## Registration

``` text
Register Form
     |
     | POST /api/auth/register
     v
AuthController
     |
     v
AuthService
     |
     +--> Validate email
     +--> BCrypt hash password
     +--> Create User
     |
     v
UserRepository
     |
     v
MySQL
     |
     v
AuthResponse
```

## Login

``` text
Login Form
     |
     v
AuthController
     |
     v
AuthService
     |
     +--> Find User
     +--> Verify BCrypt password
     |
     v
JwtService
     |
     v
JWT
     |
     v
React localStorage
```

## Authenticated request

``` text
React
 |
 | Bearer JWT
 v
JwtAuthenticationFilter
 |
 v
SecurityContext
 |
 v
Controller
 |
 v
Service
 |
 v
Repository
 |
 v
MySQL
```

## Property creation

``` text
React
 |
 | POST /api/properties
 v
Security Filter
 |
 | Authenticate user
 v
PropertyController
 |
 v
PropertyService
 |
 +--> getCurrentUser()
 +--> set owner
 |
 v
PropertyRepository
 |
 v
MySQL
 |
 v
PropertyResponse
```

## Image upload

``` text
React
 |
 | Multipart file
 v
PropertyController
 |
 v
PropertyService
 |
 v
CloudinaryService
 |
 v
Cloudinary
 |
 | secure URL
 v
PropertyImage
 |
 v
MySQL
```

## Booking

``` text
React
 |
 v
BookingController
 |
 v
BookingService
 |
 +--> Current user
 +--> Property
 +--> Date validation
 +--> Capacity validation
 +--> Availability check
 +--> Price calculation
 |
 v
BookingRepository
 |
 v
MySQL
```

## Review

``` text
React
 |
 v
ReviewController
 |
 v
ReviewService
 |
 +--> Current user
 +--> Property
 +--> Confirmed booking check
 +--> Duplicate review check
 |
 v
ReviewRepository
 |
 v
MySQL
```

------------------------------------------------------------------------

# 30. Why These Technologies?

### Spring Boot

Provides a structured framework for building REST APIs, dependency
injection, configuration, and application management.

### Spring Security + JWT

Provides stateless authentication and endpoint authorization for the
React frontend.

### BCrypt

Protects stored passwords using secure hashing.

### Spring Data JPA + Hibernate

Maps Java objects to relational MySQL tables and simplifies database
access.

### MySQL

Suitable for structured relationships between users, properties,
bookings, reviews, and images.

### Cloudinary

Stores media files externally while the application stores secure image
URLs.

### DTOs

Keep API request/response contracts separate from persistence entities.

### Service Layer

Keeps business rules such as booking availability, review eligibility,
and ownership checks outside controllers.

------------------------------------------------------------------------

# 31. Interview Concepts Demonstrated

## Java

-   OOP
-   Collections
-   Exception handling
-   Streams
-   Optional
-   BigDecimal
-   LocalDate
-   LocalDateTime

## Spring Boot

-   REST APIs
-   Dependency Injection
-   Controllers
-   Services
-   Repositories
-   Configuration
-   Validation
-   Exception handling

## Spring Security

-   JWT
-   Authentication filters
-   Authorization
-   Roles
-   Stateless sessions
-   Password encoding
-   CORS

## JPA / Hibernate

-   Entity relationships
-   One-to-many
-   Many-to-one
-   Repository query methods
-   Specifications
-   ORM
-   Entity persistence

## MySQL

-   Relational schema
-   Foreign keys
-   CRUD
-   Filtering
-   Relationships
-   Data integrity

## REST

-   GET
-   POST
-   PUT
-   PATCH
-   DELETE
-   HTTP status codes
-   JSON
-   Multipart/form-data

## Testing

-   JUnit 5
-   Mockito
-   Unit testing
-   Service testing
-   Repository testing

------------------------------------------------------------------------

# 32. Future Enhancements

Possible improvements:

-   Payment gateway
-   Email notifications
-   Booking confirmation emails
-   Password reset
-   Refresh tokens
-   Admin dashboard
-   Availability calendar
-   Pagination
-   Advanced search
-   Maps/geolocation
-   Wishlist/favorites
-   Host analytics
-   Booking workflow/status management
-   Docker
-   CI/CD
-   Production deployment
-   More controller/integration tests

------------------------------------------------------------------------

# 33. Author

**Shyam Lade**

Bachelor of Engineering -- Artificial Intelligence & Data Science

Technologies demonstrated:

``` text
Java
Spring Boot
Spring Security
JWT
Hibernate
JPA
MySQL
REST APIs
Cloudinary
JUnit
Mockito
React
Git
GitHub
```

------------------------------------------------------------------------

# 34. Summary

Wanderlust Backend demonstrates a complete secure backend architecture
for a real-world property booking platform:

``` text
Authentication
      ↓
JWT Security
      ↓
Property Management
      ↓
Cloudinary Images
      ↓
Search / Filter / Sort
      ↓
Booking Validation
      ↓
Reviews
      ↓
Host Profiles
      ↓
MySQL
```

The project focuses on:

-   Clean layered architecture
-   Secure authentication
-   Authorization and ownership checks
-   RESTful API design
-   JPA/Hibernate
-   MySQL relationships
-   Booking business rules
-   Review eligibility
-   Cloud image storage
-   Validation
-   Exception handling
-   Swagger/OpenAPI
-   Unit testing
-   React integration




