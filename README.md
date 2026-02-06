# auth_jwt
Auth with JWT in Koltin and SpringBoot

## Dependencies
Spring Boot Starter
- Spring Web
- Spring Data JPA
- Spring Security
- Validation
- MySQL Driver

Flyway DB Migration

Springdoc OpenAPI (Swagger)


## Struktur
```
de.phbe.authjwt
│
├── user                          ← Feature / Bounded Context
│   ├── api                        ← Controller / DTOs / Request-Response
│   │   ├── UserController.kt
│   │   ├── AuthController.kt
│   │   ├── dto
│   │   │   ├── UserRequest.kt
│   │   │   └── UserResponse.kt
│   │   └── auth
│   │       └── LoginRequest.kt
│   │
│   ├── service                    ← Application Layer / Use Cases
│   │   ├── UserService.kt
│   │   └── AuthService.kt
│   │
│   ├── persistence                ← Infrastruktur / DB
│   │   ├── UserEntity.kt
│   │   └── UserRepository.kt     ← Spring Data JPA
│   │
│   └── util                       ← Feature-spezifische Helfer
│       └── PasswordHasher.kt
│
├── security
│   ├── SecurityConfig.kt
│   ├── JwtTokenProvider.kt
│   └── JwtAuthenticationFilter.kt
│
├── config
│   └── OpenApiConfig.kt           ← Swagger/OpenAPI Config
│
├── exception
│   ├── GlobalExceptionHandler.kt
│   └── DomainException.kt
│
└── AuhJwtApplication.kt
```

