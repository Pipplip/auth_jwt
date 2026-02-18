# auth_jwt
Einfache Userverwaltung: Auth mit JWT (Access/Refresh-Token) in Kotlin und SpringBoot

___
## Dependencies
>Spring Boot Starter
>- Spring Web
>- Spring Data JPA
>- Spring Security
>- Validation
>- MySQL Driver
>- Flyway DB Migration 
>- Validation
>- Actuator
>- spring-boot-h2console (H2 DB für interne tests)
>- io.jsonwebtoken:jjwt (ext. lib)
>- Springdoc OpenAPI (Swagger) (ext. lib)

## ToDo

--

## Info
RefreshTokens werden nicht als HttpOnly-Cookies gesetzt. Stattdessen werden sie im Response Body zurückgegeben.  
Dies ist einfacher zu testen. Implementierung ist aber vorhanden und kann im AuthController angepasst werden.

___
## Ausführung lokal

**PROD:**
1. MySQL Server starten. Hört auf Port 3333 (kann in `application-prod.properties` bzw. `.env` angepasst werden)
2. <s>(Datenbank `userdb` anlegen)</s> --> sollte flyway automatisch machen, wenn die DB Verbindung stimmt
3. Projekt starten 
   - IDE (in Intellij in der RUN/DEBUG-configuration noch die environmental variables angeben (.env Datei auswählen) und das active profile auswählen)
   - Gradle: `./gradlew bootRun`) - http://localhost:8080
4. API testen mit API tools, Swagger oder eine client-app bauen: z.B. Registrierung, Login, Token-Refresh

**DEV:**  
In DEV Umgebung ist die H2 In-Memory DB aktiviert. Es ist keine MySQL Installation nötig.
```
Console: http://localhost:8080/h2-console
URL: jdbc:h2:mem:userdb
```

**Gradle build:**  
`./gradlew build` - baut das Projekt, führt Tests aus und erzeugt eine jar-Datei im build/libs Ordner.  
In der application-test.properties sollten keine .env Variablen verwendet werden. Nehme dort einfach dummy Werte.

___
## Umgebung auswählen

Drei Umgebungen sind angelegt: `DEV`, `TEST`, `PROD` (`application-dev.properties`, `application-test.properties`, `application-prod.properties`)

`application.properties` enthält allgemeine Konfigurationen (= Parent Config), die für alle Umgebungen gelten.  
In den spezifischen Properties-Dateien (dev, test, prod) können dann umgebungsspezifische Einstellungen überschrieben werden.

**Möglichkeiten, um die Umgebung auszuwählen:**
1. In `application.properties` die Zeile `spring.profiles.active=dev` hinzufügen (eher nein!)
2. Über die JVM-Option `-Dspring.profiles.active=dev` beim Starten der App
3. Über die Gradle-Option `-Dspring-boot.run.profiles=dev` beim Ausführen von `./gradlew bootRun`
4. Umgebungsvariable anlegen `export SPRING_PROFILES_ACTIVE=dev`
5. In Intellij: active profile in den Run/Debug Configurations auswählen (ja)
6. In Docker-Compose: SPRING_PROFILES_ACTIVE: prod (ja)

___
## Docker

`.env` = enthält Umgebundsvariablen  
Wurde commited als Beispiel. In Realität wäre .env in .gitignore

`Dockerfile` = Anweisung um Docker-Image zu bauen (Bau eines einzelnen Containers)  

`docker-compose.yml` = Konfigurationsdatei, um mehrere Container gleichzeitig zu definieren und starten.

`.dockerignore`= enthält Dateien/Dirs, die beim Bauen eines Images ignoriert werden sollen

Docker Befehle für compose (in root-Verzeichnis ausführen, wo die docker-compose.yml liegt):  
Docker Desktop davor starten!!  
- `docker compose up --build` Images neu bauen und starten
- `docker compose up` Nur starten, ohne neu zu bauen
- `docker compose stop` Alle Container stoppen (bleiben erhalten)
- `docker compose down` Alle Container stoppen und entfernen
- `docker compose down -v` Mit volumes löschen (Daten gehen verloren!)
- `docker compose pull` Aktuelle Images aktualisieren
- `docker compose logs` Logs aller Container anzeigen
- `docker compose logs -f` Logs in Echtzeit verfolgen
- `docker compose ps` Status aller Container anzeigen
- `docker image prune` Alle ungenutzten Images löschen
- `docker componse exec <container_name> bash` In einen laufenden Container wechseln (z.B. um die DB zu inspizieren)

___
## Swagger (nur in DEV aktiviert, s. securityFilterChain)

http://localhost:8080/swagger-ui/index.html

Alternativ: http://localhost:8080/swagger-ui.html

http://localhost:8080/v3/api-docs

Wenn man Token im Header eines Requests braucht, muss man dies in der OpenApiConfig.kt konfigurieren
und mit @SecurityRequirement am Endpoint angeben.

___
## Actuator (Nur in DEV aktiviert, s. Properties und securityFilterChain)

Der Actuator ist für Monitoring und health checks da.

http://localhost:8080/actuator
http://localhost:8080/actuator/health
http://localhost:8080/actuator/info
http://localhost:8080/actuator/env
http://localhost:8080/actuator/beans

___
## Bean validation

Validation gehören in die Contoller und die Input Request-DTOs von den Controllern, z.B. `LoginRequest.kt`  
Schlüsselwörter: `@field:` und `@Valid`

___
## Flyway

Flyway ist eine Art Versionsverwaltung von DB Schemas.
Struktur ist V1__name.sql (großes V mit aufsteigender Version und doppelter Unterstrich, gefolgt von einer Beschreibung + Dateiendung `.sql`).

Stelle in allen Umgebungen auf:  
`spring.jpa.hibernate.ddl-auto=validate`  
...damit Flyway allein verantwortlich für das Schema ist.

Was tun um Flyway zu verwenden? 
- Flyway aktivieren (properties): spring.flyway.enabled=true
- Initiale Migration erzeugen:
  - setze dafür in den properties spring.jpa.hibernate.ddl-auto=create
  - Hibernate erzeugt alle Tabellen
  - SQL aus der DB exportieren (als sql-Datei)
  - SQL als erste Migration speichern /db/migration/V1__init_schema.sql
- spring.jpa.hibernate.ddl-auto=validate setzen

___
## Endpoints

| Endpoint         | Methode  | Body                                  | Header                                  | Beispiel                                                  |
|------------------|----------|---------------------------------------|-----------------------------------------|-----------------------------------------------------------|
| `/auth/register` | `POST`   | JSON mit `email`, `password`          | ❌ Kein Token erforderlich               | BODY: {"email": "testuser", "password": "testpass"}       |
| `/auth/login`    | `POST`   | JSON mit `email`, `password`          | ❌ Kein Token erforderlich               |                                                           |
| `/auth/refresh`  | `POST`   | JSON mit `refreshToken`               | ❌ Kein Token erforderlich               | {"refreshToken":"037df2a8-3d21-41c2-863f-660ffe50c432"}   |
| `/auth/logout`   | `POST`   | JSON mit `refreshToken`               | ✅ `Authorization: Bearer <accessToken>` | {"refreshToken":"037df2a8-3d21-41c2-863f-660ffe50c432"}   |
| `/users/profile` | `GET`    | `-`                                   | ✅ `Authorization: Bearer <accessToken>` |                                                           |
| `/users/{id}`    | `DELETE` | Kein Body aber UUID des Users im Pfad | ✅ `Authorization: Bearer <accessToken>` | localhost:8080/users/ff16ce76-c8ea-4808-b146-e94cadeccfb2 |

___
## Begriffe

**Domäne**:  
Beschreibt ein Problem, welches man lösen will. Also Regeln, Prozesse der realen Welt. z.B. Webshop

Die Domäne enthält keine technischen Details, sondern beschreibt nur die Geschäftslogik und Regeln. Sie ist unabhängig von Frameworks, Datenbanken oder anderen Technologien.

**Bounded Context**:   
Ein abgegrenzter Bereich innerhalb einer Domäne, in dem ein bestimmtes Modell gilt. Es hilft, Komplexität zu reduzieren und klare Schnittstellen zu definieren.
Kommunikation findet über Ports statt, die die Schnittstellen zwischen den Bounded Contexts definieren.   
Jeder Kontext kann eigenständig entwickelt, getestet und bereitgestellt werden.

**Analogie:**  
`Domäne` = Landkarte / Weltkarte Bsp. Ein kompletter Webshop (enthält viele Bereiche)  
`Bounded Context` = ein Land auf der Karte (z.B. User Context: Registrierung, Login, Rollen)  
`Domain layer` innerhalb eines Bounded Contexts = die Regeln und Prozesse innerhalb dieses Landes (z.B. Passwort-Hashing, Token-Generierung) <br>

**Port**:   
Ein Interface, das die Kommunikation zwischen der Domäne und der Außenwelt definiert.  
Ein Port ist eine Schnittstelle, die sagt „Das brauche ich – aber mir ist egal, wie du es machst.

**Bsp:** Stell dir ein Haus vor.   
Die Logik lebt im Haus, die Außenwelt ist Strom, Wasser und Internet.  
Dein Haus sagt: Ich brauch Strom.   
Dann ist der Stromanschluss der Port und das Kraftwerk der Adapter.

Port (innen) z.B. `interface PasswordHasher`: Ich brauche jemanden, der Passwörter hasht.  
Adapter (aussen): `class BCryptPasswordHasher : PasswordHasher`

Macht alles flexibler und austauschbar und besser testbar.

**Arten von Ports:**
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

**Allgemein:**

`Controller` = Schnittstelle nach außen. Ein Controller nimmt Requests an, leitet diese an den Service und gibt ein Response zurück.  
`Service` = Use Case / Logik-Knoten   
`Ports` = Interface für Abhängigkeiten nach außen  
`Adapter` = Implementierung dieser Ports   

___
### Repository Beziehungen (Namenskonventionen)

```
Application / Service Layer
↓

UserRepository (Domain Interface) 
= Domain Port, kennt nur Domain-Modell, nichts über DB oder Spring!
= Vertrag! Was brauche ich?
↓

SpringUserRepository (Adapter) 
= nutzt intern Spring data, mapped zwischen Domain und JPA Entity
= Übersetzer zwischen den Repos
↓

JpaUserRepository (Spring Data Zugriff) 
= Wie wird ausgeführt?
= Diese Repo bringt automatisch CRUD-Methoden mit:
- save
- saveAll
- saveAndFlush
- saveAllAndFlush
- findById
- findAll
- findAllById
- findAll(Pageable)
- findAll(Sort)
- count
- existsById
- getReferenceById
- delete
- deleteById
- deleteAll
- deleteAll(Iterable)
- deleteAllById
- deleteAllInBatch
- deleteAllByIdInBatch
- deleteAllInBatch(Iterable)
Die nicht explizit deklariert werden müssen.
↓

Hibernate / JPA
↓

Datenbank
```

### Spring Data Namenskonvention

**Code-Beispiel: `UserRepository` (Domain Interface)**

Das `UserRepository` ist kein Spring Data Interface, d.h. es kann heißen wie es will und kann Funktionen benennen wie es will.  
Es ist ein normales Kotlin-Interface.

Erst im `JpaUserRepository` ist die Namenskonvention wichtig, weil dies Spring Data JPA ist.  
Spring analysiert die Methodennamen und erzeugt daraus automatisch SQL.

**Naming-Strategy**

Allgemein wird:  
`CamelCase` zu `snake_case`  
und `Groß` zu `klein`-Schreibung  

Bsp:  
`UserJpaRepo` wird zu `user_jpa_repo`  
`userRole` wird zu `user_role`  
`registeredAt` wird zu `registered_at`  

**Wichtig!!**
Nur im JpaUserRepository müssen Methodennamen:
- mit `find`, `exists`, `delete`, `count` beginnen
- `By` enthalten
- exakt die PropertNamen der Entitiy benutzen  

Weil dies Spring JPA ist und Spring Data JPA die Methodennamen analysiert, um SQL zu generieren.

**Bsp: Entity**    
`val email` --zu--> `findByEmail`  
`val userRole` --zu--> `findByUerRole`

Man kann Namenskonventionen auch manuell überschreiben z.B.:
```
@Table(name = "users")
@Column(name = "password_hash")
@Column(name = "registered_at")
```

Hier müssen die Funktionsnamen dann so aussehen:  
`findByPasswordHash()`  
`findByRegisteredAt()`  

**@Query**  
wird verwendet wenn man komplexe SQL Queries verwendet. z.B. Joins etc.  
Wenn einfache Queries nicht mit der Namenskonvention abgebildet werden können, verwendet man eigene `@Query`-Annotationen.  

```
@Query("""
SELECT u FROM UserJpaEntity u
JOIN u.orders o
WHERE o.totalAmount > :amount
""")
fun findUsersWithLargeOrders(amount: BigDecimal)
```
Native Queries:
```
@Query(
value = "SELECT * FROM users WHERE password_hash = ?1",
nativeQuery = true
)
fun findByPasswordHashNative(hash: String)
```

### Übersicht der Schlüsselwörter:

| Prefix   | Bedeutung |
| -------- | --------- |
| findBy   | SELECT    |
| readBy   | SELECT    |
| getBy    | SELECT    |
| queryBy  | SELECT    |
| countBy  | COUNT     |
| existsBy | EXISTS    |
| deleteBy | DELETE    |
| removeBy | DELETE    |

| Schlüsselwort | Beispiel                              |
| ------------- | ------------------------------------- |
| And           | findByEmailAndUserRole                |
| Or            | findByEmailOrUserRole                 |
| Between       | findByRegisteredAtBetween             |
| LessThan      | findByAgeLessThan                     |
| GreaterThan   | findByAgeGreaterThan                  |
| Like          | findByEmailLike                       |
| Containing    | findByEmailContaining                 |
| StartingWith  | findByEmailStartingWith               |
| EndingWith    | findByEmailEndingWith                 |
| In            | findByIdIn                            |
| IsNull        | findByDeletedAtIsNull                 |
| IsNotNull     | findByDeletedAtIsNotNull              |
| True          | findByActiveTrue                      |
| False         | findByActiveFalse                     |
| OrderBy       | findByUserRoleOrderByRegisteredAtDesc |

___
## Struktur
```
de.phbe.authjwt
├── user/
│   ├── domain/
│   │   ├── exception/
│   │   │   ├── UserNotFoundException.kt
│   │   │   ├── InvalidCredentialsException.kt
│   │   │   ├── InvalidRefreshTokenException.kt
│   │   │   ├── RefreshTokenExpiredException.kt
│   │   │   ├── UnauthorizedException.kt
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
│   │       ├── RefreshTokenHasher.kt
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
│   ├── JwtProperties.kt
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
