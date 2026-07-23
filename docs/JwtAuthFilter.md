# JwtAuthFilter — Detailed Explanation

This document explains `JwtAuthFilter` and how it fits into this project's JWT-based Spring Security setup.

**Source file:**  
`src/main/java/com/example/SpringDataJpaDemo/security/JwtAuthFilter.java`

---

## Simple flows: unauthenticated vs authenticated

Two common cases. Keep this picture in mind:

```
Request → JwtAuthFilter → SecurityConfig (rules) → Controller (or error)
```

### A) Unauthenticated user (no JWT / not logged in)

**What “unauthenticated” means:** no valid `Authorization: Bearer <token>` header, so `SecurityContext` stays empty.

#### A1) Hits a **public** route (allowed without login)

Examples: `POST /api/v1/auth/login`, `POST /api/v1/auth/register`, `POST /api/v1/users`

```
Unauthenticated client
        │
        ▼
JwtAuthFilter
  - no Bearer header
  - does NOT set authentication
  - continues chain
        │
        ▼
SecurityConfig
  - path is permitAll()
  - allows request
        │
        ▼
Controller runs → 200 / normal response
```

**Simple idea:** filter ignores missing token; config says “this URL is open.”

#### A2) Hits a **protected** route (needs login)

Examples: `GET /api/v1/me`, admin user APIs, anything under `.authenticated()` / `hasRole(...)`

```
Unauthenticated client
        │
        ▼
JwtAuthFilter
  - no Bearer header
  - does NOT set authentication
  - continues chain
        │
        ▼
SecurityConfig
  - path requires auth / role
  - SecurityContext is empty → not logged in
        │
        ▼
401 Unauthorized
{ "code": "UNAUTHORIZED", "message": "Authentication required" }
```

**Simple idea:** filter does not block by itself when token is missing; **SecurityConfig** blocks protected URLs with **401**.

---

### B) Authenticated user (valid JWT)

**What “authenticated” means:** client sends a valid Bearer token; filter loads the user and fills `SecurityContext`.

#### B1) Token valid + role allowed for that URL

```
Authenticated client
  Header: Authorization: Bearer <valid-jwt>
        │
        ▼
JwtAuthFilter
  - parse JWT → email
  - CustomUserDetailsService loads user + roles
  - set SecurityContext (authenticated)
  - continue chain
        │
        ▼
SecurityConfig
  - checks hasRole / hasAnyRole
  - role matches → allow
        │
        ▼
Controller runs
  - @AuthenticationPrincipal has the user
  - business logic → 200 / normal response
```

**Simple idea:** filter proves *who you are*; config checks *if your role may call this URL*.

#### B2) Token valid but role **not** allowed

Example: USER calls an ADMIN-only path like `/api/v1/users/**`

```
Authenticated USER
        │
        ▼
JwtAuthFilter → sets SecurityContext (ROLE_USER)
        │
        ▼
SecurityConfig → needs ROLE_ADMIN → deny
        │
        ▼
403 Forbidden
{ "code": "FORBIDDEN", "message": "Access denied" }
```

#### B3) Token invalid or expired

```
Client with bad/expired JWT
        │
        ▼
JwtAuthFilter
  - parseToken fails
  - returns 401 immediately
  - controller never runs
        │
        ▼
401 Unauthorized
{ "code": "UNAUTHORIZED", "message": "Invalid or expired token" }
```

---

### Quick comparison

| Who | Route type | Who decides | Result |
|---|---|---|---|
| Unauthenticated | Public (`permitAll`) | SecurityConfig allows | Controller runs |
| Unauthenticated | Protected | SecurityConfig blocks | **401** Authentication required |
| Authenticated (good token + right role) | Protected | Filter + SecurityConfig allow | Controller runs |
| Authenticated (good token + wrong role) | Protected | SecurityConfig blocks | **403** Access denied |
| Bad / expired token | Any (with Bearer header) | **JwtAuthFilter** blocks | **401** Invalid or expired token |

**Remember:**

- **401** = not logged in (or bad token) → “who are you?”
- **403** = logged in but not allowed → “I know you, but you can’t do this”

---

## 1. What problem does it solve?

This API is **stateless**. There is no server-side HTTP session that remembers who is logged in.

After login, the client receives a **JWT** (JSON Web Token). On every later request, the client must send that token in the header:

```http
Authorization: Bearer <jwt-token-here>
```

`JwtAuthFilter` is the component that:

1. Reads that header
2. Validates the JWT
3. Loads the user (and roles) from the database
4. Puts the authenticated user into Spring Security's `SecurityContext`
5. Lets the rest of the request continue (or returns `401` if the token is bad)

Without this filter, Spring Security would never know who the caller is on protected endpoints.

---

## 2. Where it sits in the request pipeline

```
Client HTTP request
        │
        ▼
┌───────────────────────────────────────┐
│  Spring Security Filter Chain         │
│                                       │
│  1. JwtAuthFilter  ◄── our custom     │
│       (runs first for JWT)            │
│                                       │
│  2. authorizeHttpRequests             │
│       (SecurityConfig role checks)    │
│                                       │
│  3. Controller                        │
│       (MeController, etc.)            │
└───────────────────────────────────────┘
```

It is registered in `SecurityConfig` to run **before** Spring's default username/password filter:

```java
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```

Also, servlet auto-registration of the filter is **disabled** so it only runs inside the Security filter chain (once per request), not twice.

---

## 3. Class structure

```java
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(...) { ... }
}
```

| Piece | Meaning |
|---|---|
| `@Component` | Spring creates a bean so `SecurityConfig` can inject it |
| `@RequiredArgsConstructor` | Lombok generates a constructor for the two `final` fields |
| `OncePerRequestFilter` | Guarantees this filter runs **at most once** per request |
| `JwtService` | Parses and verifies JWT signature/expiry |
| `UserDetailsService` | Loads user + roles from DB (`CustomUserDetailsService` in practice) |

---

## 4. Full flow (step by step)

### Step A — Read the `Authorization` header

```java
String header = request.getHeader("Authorization");
if (header == null || !header.startsWith("Bearer ")) {
    filterChain.doFilter(request, response);
    return;
}
```

| Case | What happens |
|---|---|
| No header | Filter does nothing; continues the chain |
| Header not starting with `Bearer ` | Same — continue without authenticating |
| Valid `Bearer <token>` | Proceed to parse the token |

**Important:** Missing token does **not** always mean failure here. Public routes like `/api/v1/auth/**` are allowed without auth by `SecurityConfig`. Protected routes will fail later with `401 Authentication required` if no authentication was set.

The space after `Bearer` matters:

```text
✅ Authorization: Bearer eyJhbGciOi...
❌ Authorization: Bearer eyJhbGciOi...   (wrong format still needs "Bearer ")
❌ Authorization: eyJhbGciOi...          (missing Bearer prefix)
```

---

### Step B — Extract and parse the JWT

```java
String token = header.substring(7); // strip "Bearer "
Claims claims = jwtService.parseToken(token);
String email = claims.getSubject();
```

- `substring(7)` removes the literal `"Bearer "` (7 characters).
- `jwtService.parseToken(token)`:
  - Verifies the signature with the secret key
  - Checks expiry
  - Returns JWT claims (payload)
- `claims.getSubject()` is the **email**, because at login we store email as the JWT subject:

```java
// JwtService.generateJwtToken
.subject(userDetails.getUsername())  // username = email in this project
```

If the token is expired, tampered, or malformed, `parseToken` throws — handled in the `catch` block (Step E).

---

### Step C — Load the user from the database

```java
if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    UserDetails user = userDetailsService.loadUserByUsername(email);
    // ...
}
```

The JWT only proves **identity** (this email logged in recently).  
It does **not** (in this project) store roles.

So the filter reloads the user via `CustomUserDetailsService`:

1. Look up user by email in the DB
2. Map entity → Spring `UserDetails` with:
   - username = email
   - password = hashed password (not used after JWT auth)
   - roles = `ROLE_USER` / `ROLE_ADMIN` (from `user.getRole().name()`)

**Why reload from DB instead of trusting only the JWT?**

- Confirm the user still exists
- Pick up **current** roles (e.g. demoted from ADMIN → USER)
- Build a proper Spring Security principal for controllers

The guard `getAuthentication() == null` avoids overwriting an authentication that was already set on this request.

---

### Step D — Put authentication into `SecurityContext`

```java
UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
SecurityContextHolder.getContext().setAuthentication(auth);
```

| Argument | Value | Why |
|---|---|---|
| principal | `UserDetails user` | Who is logged in |
| credentials | `null` | Password not needed after JWT validation |
| authorities | `user.getAuthorities()` | Roles used by `hasRole(...)` checks |

After this:

- `SecurityConfig` can authorize with `hasRole("ADMIN")`, `hasAnyRole("ADMIN", "USER")`, etc.
- Controllers can inject the user with `@AuthenticationPrincipal UserDetails userDetails`

Example in `MeController`:

```java
@GetMapping
public ResponseEntity<UserDto> me(@AuthenticationPrincipal UserDetails userDetails) {
    // userDetails.getUsername() == email from JWT / CustomUserDetailsService
}
```

---

### Step E — Invalid or expired token

```java
} catch (Exception exception) {
    SecurityContextHolder.clearContext();
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getOutputStream().write(
            "{\"code\":\"UNAUTHORIZED\",\"message\":\"Invalid or expired token\"}"
                    .getBytes(StandardCharsets.UTF_8));
    return; // stop — do not call controller
}
```

If anything fails while parsing the token or loading the user:

1. Clear any partial security context
2. Return HTTP **401** with a JSON body
3. **Do not** call `filterChain.doFilter` — the request ends here

---

### Step F — Continue the chain on success (or no Bearer header)

```java
filterChain.doFilter(request, response);
```

Control moves to the next filters, then role checks in `SecurityConfig`, then the controller.

---

## 5. Decision table

| Request situation | Filter behavior | Typical final result |
|---|---|---|
| Public URL, no token | Skip auth setup, continue | Allowed by `permitAll()` |
| Protected URL, no token | Skip auth setup, continue | `401 Authentication required` from SecurityConfig |
| Valid token + allowed role | Set SecurityContext, continue | Controller runs (200/etc.) |
| Valid token + wrong role | Set SecurityContext, continue | `403 Access denied` from SecurityConfig |
| Invalid / expired token | Write 401 JSON, stop | `401 Invalid or expired token` from filter |
| Valid token but user deleted from DB | Exception while loading user → 401 | `401 Invalid or expired token` |

---

## 6. How it connects to the rest of security

### Login (token creation) — **not** done by this filter

```
POST /api/v1/auth/login
  → AuthService
  → AuthenticationManager + CustomUserDetailsService (password check)
  → JwtService.generateJwtToken(...)
  → client stores JWT
```

### Later protected request — **this filter**

```
GET /api/v1/me
  Header: Authorization: Bearer <jwt>
  → JwtAuthFilter
      → JwtService.parseToken
      → CustomUserDetailsService.loadUserByUsername(email)
      → SecurityContext.setAuthentication
  → SecurityConfig: hasAnyRole("ADMIN", "USER")
  → MeController
```

### Component responsibilities

| Class | Responsibility |
|---|---|
| `JwtService` | Create and parse JWTs |
| `CustomUserDetailsService` | Load user + roles from DB as `UserDetails` |
| **`JwtAuthFilter`** | Bridge: HTTP Bearer token → authenticated SecurityContext |
| `SecurityConfig` | URL access rules (`permitAll`, `hasRole`, etc.) |
| Controllers | Business logic; read user via `@AuthenticationPrincipal` |

---

## 7. End-to-end example

### Request

```http
GET /api/v1/me HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Inside `JwtAuthFilter`

1. Header starts with `Bearer ` → extract token  
2. `parseToken` succeeds → subject = `alice@example.com`  
3. `loadUserByUsername("alice@example.com")` → `UserDetails` with `ROLE_USER`  
4. `SecurityContext` now has authenticated principal `alice@example.com`  

### Inside `SecurityConfig`

- Path `/api/v1/me` requires `hasAnyRole("ADMIN", "USER")`
- User has `ROLE_USER` → allowed  

### Inside `MeController`

- `@AuthenticationPrincipal` injects that `UserDetails`
- Service loads profile by email and returns JSON  

---

## 8. Common misconceptions

### “The filter checks roles”
No. The filter only **authenticates** (who is this?).  
**Authorization** (is this role allowed?) is done by `SecurityConfig` after the filter sets the SecurityContext.

### “Roles come from the JWT”
Not in this project. The JWT subject is only the email. Roles are reloaded from the database by `CustomUserDetailsService` on every authenticated request.

### “No Bearer header always means 401 from the filter”
No. The filter simply continues without setting authentication. Public endpoints still work. Protected endpoints fail later in the security chain.

### “JwtAuthFilter replaces CustomUserDetailsService”
No. The filter **uses** `UserDetailsService` (`CustomUserDetailsService`). They work together:

- Filter = request-level JWT plumbing  
- UserDetailsService = DB → Spring Security user mapping  

---

## 9. Source walkthrough (annotated)

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    // 1) Look for Authorization: Bearer <jwt>
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
        filterChain.doFilter(request, response); // public or unauthenticated path
        return;
    }

    try {
        // 2) Validate JWT and read email (subject)
        String token = header.substring(7);
        Claims claims = jwtService.parseToken(token);
        String email = claims.getSubject();

        // 3) Load user + roles; store Authentication for this request
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    } catch (Exception exception) {
        // 4) Bad token / missing user → stop with 401
        SecurityContextHolder.clearContext();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(
                "{\"code\":\"UNAUTHORIZED\",\"message\":\"Invalid or expired token\"}"
                        .getBytes(StandardCharsets.UTF_8));
        return;
    }

    // 5) Continue → role checks → controller
    filterChain.doFilter(request, response);
}
```

---

## 10. One-line summary

**`JwtAuthFilter` turns `Authorization: Bearer <jwt>` into a Spring Security authenticated user (with roles from the DB) for the current request — or returns 401 if the token is invalid.**
