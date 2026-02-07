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

## Begriffe

**Domäne**: Beschreibt ein Problem, welches man lösen will. Also Regeln, Prozesse der realen Welt. z.B. Webshop

Die Domäne enthält keine technischen Details, sondern beschreibt nur die Geschäftslogik und Regeln. Sie ist unabhängig von Frameworks, Datenbanken oder anderen Technologien.

**Bounded Context**: Ein abgegrenzter Bereich innerhalb einer Domäne, in dem ein bestimmtes Modell gilt. Es hilft, Komplexität zu reduzieren und klare Schnittstellen zu definieren.
Kommunikation findet über Ports statt, die die Schnittstellen zwischen den Bounded Contexts definieren.
Jeder Kontext kann eigenständig entwickelt, getestet und bereitgestellt werden.

Analogie:
Domäne = Landkarte / Weltkarte Bsp. Ein kompletter Webshop (enthält viele Bereiche)<br>
Bounded Context = ein Land auf der Karte (z.B. User Context: Registrierung, Login, Rollen) <br>
Domain layer innerhalb eines Bounded Contexts = die Regeln und Prozesse innerhalb dieses Landes (z.B. Passwort-Hashing, Token-Generierung) <br>

**Port**: Ein Interface, das die Kommunikation zwischen der Domäne und der Außenwelt definiert

## Struktur
```
de.phbe.authjwt
├── user/
│   ├── domain/
│   │   ├── exception/
│   │   │   └── UserAlreadyExistsException.kt
│   │   ├── model/
│   │   │   ├── User.kt
│   │   │   └── UserId.kt
│   │   └── repository/  ← Outbound Port
│   │       └── UserRepository.kt
│   ├── adapter/
│   │   ├── persistence/
│   │   │   ├── SpringUserRepository.kt
│   │   │   ├── UserJpaEntity.kt
│   │   │   └── UserMapper.kt
│   │   └── security/
│   │       ├── BCryptPasswordHasher.kt
│   │       └── JwtTokenProvider.kt
│   ├── service/ ← Application / Use Cases
│   │   └── UserService.kt
│   ├── security/
│   │   └── PasswordHasher.kt
│   └── web/  ← Inbound Adapter
│       ├── dto/
│       │   ├── LoginRequest.kt
│       │   ├── JwtResponse.kt
│       │   ├── RegisterRequest.kt
│       │   └── UserResponse.kt
│       ├── AuthController.kt
│       └── UserController.kt
├── security/
│   ├── JwtAuthenticationFilter.kt
│   └── SecurityConfig.kt
├── exception/
│   └── GlobalExceptionHandler.kt
├── config/
│   └── OpenApiConfig.kt
└── AuthJwtApplication.kt

```
OLD
```
de.phbe.authjwt
│
├── user (Bounded Context / Feature)
│   ├── domain
│   │   ├── model
│   │   │   ├── User.kt
│   │   │   └── UserId.kt
│   │   ├── repository
│   │   │   └── UserRepository.kt        <- Port (Interface)
│   │   └── exception
│   │       └── UserAlreadyExistsException.kt
│   │
│   ├── application
│   │   ├── port
│   │   │   ├── in
│   │   │   │   ├── RegisterUserUseCase.kt
│   │   │   │   └── AuthenticateUserUseCase.kt
│   │   │   └── out
│   │   │       └── SaveUserPort.kt
│   │   └── usecase
│   │       ├── RegisterUserService.kt
│   │       └── AuthenticateUserService.kt
│   │
│   └── adapter
│       ├── web
│       │   ├── UserController.kt
│       │   ├── AuthController.kt
│       │   └── dto
│       │       ├── RegisterRequest.kt
│       │       ├── LoginRequest.kt
│       │       └── JwtResponse.kt
│       ├── persistence
│       │   ├── UserJpaEntity.kt
│       │   ├── SpringUserRepository.kt
│       │   └── UserMapper.kt
│       └── security
│           ├── BCryptPasswordHasher.kt
│           └── JwtTokenProvider.kt
│
├── config
│   └── OpenApiConfig.kt
│
├── security
│   ├── SecurityConfig.kt
│   └── JwtAuthenticationFilter.kt
│
├── exception
│   └── GlobalExceptionHandler.kt
│
└── AuthJwtApplication.kt
```


OLD
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

