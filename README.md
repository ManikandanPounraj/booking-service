# BookingService

**Port**: `8083`

## 📘 Description
Handles booking of event tickets and cancellation. Communicates with User and Event services.

## 📡 API Endpoints
- `POST /api/bookings?userId=&eventId=&seats= - Create booking`
- `GET /api/bookings/{id} - Get booking by ID`
- `GET /api/bookings/user/{userId} - Get bookings by user`
- `DELETE /api/bookings/{id} - Cancel booking`

## ▶️ How to Run
1. Make sure you have Java 17+ and Maven installed.
2. Navigate to this microservice folder.
3. Run `mvn spring-boot:run`

## 🔍 Swagger UI
- Visit: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
