# auth_jwt
Einfache Userverwaltung: Auth mit JWT (Access/Refresh-Token) in Kotlin und SpringBoot

## Dependencies
Spring Boot Starter
- Spring Web
- Spring Data JPA
- Spring Security
- Validation
- MySQL Driver

Flyway DB Migration

Springdoc OpenAPI (Swagger)

## ToDo

1. Flyway DB Migration hinzufügen
2. Swagger UI anpassen (z.B. Titel, Beschreibung, API-Gruppierung)
3. Dockerfile erstellen für MySQL und die Spring Boot App

## Ausführung

1. MySQL Server starten. Hört auf Port 3333 (kann in `application.properties` angepasst werden)
2. Datenbank `userdb` anlegen
3. Projekt starten (z.B. über IDE oder `./gradlew bootRun`) - http://localhost:8080
4. Swagger UI: http://localhost:8080/swagger-ui.html
5. API testen mit API tools oder eine client-app bauen: z.B. Registrierung, Login, Token-Refresh

## Umgebung auswählen

Drei Umgebungen sind angelegt: DEV, TEST, PROD. (`application-dev.properties`, `application-test.properties`, `application-prod.properties`)

Möglichkeiten, um die Umgebung auszuwählen:
1. In `application.properties` die Zeile `spring.profiles.active=dev` hinzufügen
2. Über die JVM-Option `-Dspring.profiles.active=dev` beim Starten der App
3. Über die Gradle-Option `-Dspring-boot.run.profiles=dev` beim Ausführen von `./gradlew bootRun`
4. Umgebungsvariable anlegen `export SPRING_PROFILES_ACTIVE=dev`

**Info:** RefreshTokens werden nicht als HttpOnly-Cookies gesetzt. Stattdessen werden sie im Response Body zurückgegeben.
Dies ist einfacher zu testen. Implementierung ist aber vorhanden und kann im AuthController angepasst werden.

## Endpoints

| Endpoint         | Methode  | Body                                  | Header                                   | Beispiel                                                  |
|------------------|----------|---------------------------------------|------------------------------------------|-----------------------------------------------------------|
| `/auth/register` | `POST`   | JSON mit `email`, `password`          | ❌ Kein Token erforderlich                | BODY: {"email": "testuser", "password": "testpass"}       |
| `/auth/login`    | `POST`   | JSON mit `email`, `password`          | ❌ Kein Token erforderlich                |                                                           |
| `/auth/refresh`  | `POST`   | JSON mit `refreshToken`               | ✅ `Authorization: Bearer <accessToken>`  | {"refreshToken":"037df2a8-3d21-41c2-863f-660ffe50c432"}   |
| `/auth/logout`   | `POST`   | JSON mit `refreshToken`               | ✅ `Authorization: Bearer <accessToken>`  | {"refreshToken":"037df2a8-3d21-41c2-863f-660ffe50c432"}   |
| `/users/profile` | `GET`    | `-`                                   | ✅ `Authorization: Bearer <accessToken>`  |                                                           |
| `/users/{id}`    | `DELETE` | Kein Body aber UUID des Users im Pfad | ✅ `Authorization: Bearer <accessToken>`  | localhost:8080/users/ff16ce76-c8ea-4808-b146-e94cadeccfb2 |

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
Ein Port ist eine Schnittstelle, die sagt „Das brauche ich – aber mir ist egal, wie du es machst.

Bsp: Stell dir ein Haus vor. Die Logik lebt im Haus, die Außenwelt ist Strom, Wasser Internet.
Dein Haus sagt: Ich brauch Strom.
Dann ist der Stromanschluss der Port und das Kraftwerk der Adapter.

Port (innen) z.B. interface PasswordHasher: Ich brauche jemanden, der Passwörter hasht.
Adapter (aussen): class BCryptPasswordHasher : PasswordHasher

Macht alles flexibler und austauschbar und besser testbar.

Arten von Ports:
1. Inbound Ports: Was darf man mit mir tun? (login, register User ...) Meist Service Interfaces
2. Outbound Ports: Was brauche ich von außen? (Token erzeugen, Daten speichern, Passwort prüfen)

```
           ┌─────────────────────────┐
           │        Controller       │  ← Inbound Adapter
           │-------------------------│
           │ AuthController          │
           │ UserController          │
           └─────────┬───────────────┘
                     │ ruft
                     ▼
           ┌─────────────────────────┐
           │       Service /         │  ← Use Cases / Application Layer
           │    UserService          │
           │    AuthService          │
           └─────────┬───────────────┘
                     │ nutzt im Fall UserService
          ┌──────────┴───────────┐
          │                      │
          ▼                      ▼
┌──────────────────┐     ┌─────────────────┐
│   UserRepository │     │ PasswordHasher  │  ← Outbound Ports (Interfaces)
│   (Interface)    │     │ (Interface)     │
└─────────┬────────┘     └─────────┬───────┘
          │                        │ implementiert
          ▼                        ▼
┌─────────────────┐     ┌─────────────────┐
│SpringUserRepo   │     │ BCryptPassword  │  ← Adapter / Implementierungen
│ (JPA / DB)      │     │ Hasher          │
└─────────────────┘     └─────────────────┘

                ▲
                │
           Domain Layer
        ┌─────────────┐
        │ User.kt     │
        │ UserId.kt   │
        │ Exceptions  │
        └─────────────┘

```

Controller = Schnittstelle von außen

Service = Use Case / Logik-Knoten

Ports = Interface für Abhängigkeiten nach außen

Adapter = Implementierung dieser Ports

## Struktur
```
de.phbe.authjwt
├── user/
│   ├── domain/
│   │   ├── exception/
│   │   │   ├── UserNotFoundException.kt
│   │   │   └── UserAlreadyExistsException.kt
│   │   ├── model/
│   │   │   ├── RefreshToken.kt
│   │   │   ├── User.kt
│   │   │   ├── UserId.kt
│   │   │   └── UserRole.kt
│   │   └── repository/  (← Outbound Port)
│   │       ├── RefreshTokenRepository.kt
│   │       └── UserRepository.kt
│   ├── adapter/
│   │   ├── persistence/
│   │   │   ├── JpaRefreshTokenRepository.kt
│   │   │   ├── RefreshTokenJpaEntity.kt
│   │   │   ├── RefreshTokenMapper.kt
│   │   │   ├── SpringRefreshTokenRepository.kt
│   │   │   ├── SpringUserRepository.kt
│   │   │   ├── UserJpaEntity.kt
│   │   │   ├── JpaUserRepository.kt
│   │   │   └── UserMapper.kt
│   │   └── security/
│   │       └── BCryptPasswordHasher.kt
│   ├── service/ (← Application / Use Cases)
│   │   │── AuthService.kt
│   │   └── UserService.kt
│   ├── security/
│   │   └── PasswordHasher.kt (← Outbound Port)
│   └── web/  (← Inbound Adapter)
│       ├── dto/
│       │   ├── LoginRequest.kt
│       │   ├── AuthTokens.kt
│       │   ├── RefreshRequest.kt
│       │   ├── RegisterRequest.kt
│       │   └── UserResponse.kt
│       ├── AuthController.kt
│       └── UserController.kt
├── security/
│   ├── JwtAuthenticationFilter.kt
│   ├── SecurityConfig.kt
│   └── JwtTokenProvider.kt
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
