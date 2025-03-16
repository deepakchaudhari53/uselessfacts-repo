# Useless Facts Backend

A Quarkus-based service to fetch and cache random facts.

## Prerequisites
- Java 17+
- Maven 3.9.3

## Running
```bash
mvn quarkus:dev

#### Swagger UI - http://localhost:8080/q/swagger-ui/

Use the following header to access admin endpoints:
```bash
curl -H "X-API-Key: secret_admin_key" http://localhost:8080/facts/admin/statistics

### Explanation

1. **Data Handling:**
   - `ApiFactResponse` mirrors the structure of the external API's response.
   - `CachedFact` extends this with a `shortenedUrl` (UUID) and `accessCount`.

2. **Caching:**
   - `FactService` uses a `ConcurrentHashMap` for thread-safe in-memory storage.
   - Atomic operations (via `computeIfPresent`) ensure safe access count increments.

3. **Endpoints:**
   - **POST /facts:** Fetches from external API, generates a UUID, and stores the fact.
   - **GET /facts/{id}:** Retrieves and increments access count atomically.
   - **GET /facts:** Returns all facts without modifying counts.
   - **GET /admin/statistics:** Provides access statistics for monitoring.

4. **Error Handling:**
   - Gracefully handles external API failures with appropriate HTTP status codes.

5. **Testing:**
   - Mocked REST client in tests ensures reliability and speed.
   - Comprehensive tests cover success and failure scenarios.

## Endpoints
| Method | Endpoint                            | Description                                                                           |
|--------|-------------------------------------|---------------------------------------------------------------------------------------|
| `POST`  | `/v1/facts`                        | Get a random fact from the Useless Facts API, stores it, and returns a shortened URL. |
| `GET`  | `/v1/facts/{shortenedUrl}`          | Returns the cached fact and increments the access count.                              |
| `GET`  | `/v1/facts/getAllFacts`             | Returns all cached facts and does not increment the access count.                     |
| `GET`  | `/v1/facts/{shortenedUrl}/redirect` | Redirects to the original fact and increments the access count.                       |
| `GET`  | `/v1/facts/admin/statistics`        | Provides access statistics for all shortened URLs.                                    |