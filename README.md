# 🔐 Пример JWT Аутентификации

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=spring)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green?style=for-the-badge&logo=mongodb)
![Redis](https://img.shields.io/badge/Redis-7.2-red?style=for-the-badge&logo=redis)
![Gradle](https://img.shields.io/badge/Gradle-8.5-blue?style=for-the-badge&logo=gradle)

**Реактивное Spring Boot приложение с JWT аутентификацией**

[Возможности](#-возможности) • [Быстрый старт](#-быстрый-старт) • [Документация API](#-api-endpoints) • [Архитектура](#-архитектура)

</div>

---

## 📋 Содержание

- [Возможности](#-возможности)
- [Технологический стек](#-технологический-стек)
- [Быстрый старт](#-быстрый-старт)
- [Архитектура](#-архитектура)
- [Паттерны проектирования](#-паттерны-проектирования)
- [Зависимости](#-зависимости)
- [API Endpoints](#-api-endpoints)
- [Работа с Postman](#-работа-с-postman)
- [Безопасность](#-безопасность)
- [Конфигурация](#-конфигурация)
- [Тестирование](#-тестирование)

## ✨ Возможности

- 🔐 **JWT Аутентификация** - Безопасная токен-ориентированная аутентификация с алгоритмом HS512
- 🔄 **Refresh Токены** - Одноразовые refresh токены, хранящиеся в Redis
- ⚡ **Реактивная архитектура** - Неблокирующие операции с Spring WebFlux
- 🗄️ **Интеграция MongoDB** - Реактивный MongoDB для хранения пользователей
- 💾 **Интеграция Redis** - Быстрый поиск refresh токенов с hash индексацией
- 🛡️ **Spring Security** - Безопасность на уровне методов с `@PreAuthorize`
- 🔑 **Контроль доступа на основе ролей** - Роли USER, ADMIN, MANAGER
- 🚀 **Поддержка Docker** - Простая настройка с Docker Compose

## 🛠 Технологический стек

| Категория | Технология |
|-----------|-----------|
| **Язык** | Java 21 |
| **Фреймворк** | Spring Boot 3.3.4 |
| **Веб** | Spring WebFlux (Реактивный) |
| **Безопасность** | Spring Security Reactive |
| **База данных** | MongoDB 7.0 (Реактивный) |
| **Кэш** | Redis 7.2 (Реактивный) |
| **JWT** | JJWT 0.11.1 |
| **Инструмент сборки** | Gradle |
| **Утилиты** | Lombok |

## 🚀 Быстрый старт

### Предварительные требования

- ☕ Java 21 или выше
- 🐳 Docker и Docker Compose
- 📦 Gradle (или используйте `./gradlew`)

### Установка

1. **Клонируйте репозиторий**
   ```bash
   git clone https://github.com/yourusername/jwt-example.git
   cd jwt-example
   ```

2. **Запустите инфраструктуру (MongoDB & Redis)**
   ```bash
   docker-compose -f docker/docker-compose.yml up -d
   ```

3. **Настройте приложение**
   
   Отредактируйте `src/main/resources/application.yml`:
   ```yaml
   user-service:
     jwt:
       secret: <ваш-секретный-ключ-минимум-64-символа>
   ```

4. **Соберите и запустите**
   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

5. **Проверьте работу**
   
   Приложение будет доступно по адресу `http://localhost:8080`

### Быстрый тест

```bash
# Создать пользователя
curl -X POST http://localhost:8080/api/v1/public/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "roles": ["ROLE_USER"]
  }'

# Получить JWT токен
curl -X POST http://localhost:8080/api/v1/public/token/password \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

## 🏗 Архитектура

### Многослойная архитектура

```
┌─────────────────────────────────────┐
│   Web Layer (Controllers)           │  ← REST API endpoints
├─────────────────────────────────────┤
│   Service Layer                     │  ← Бизнес-логика
├─────────────────────────────────────┤
│   Repository Layer                  │  ← Доступ к данным
├─────────────────────────────────────┤
│   Security Layer                    │  ← Аутентификация/Авторизация
└─────────────────────────────────────┘
```

### Структура пакетов

```
com.github.seecret.jwtexample/
├── configuration/          # Конфигурационные классы
│   ├── RedisConfiguration.java
│   └── SecurityConfiguration.java
├── entity/                 # Сущности данных
│   ├── User.java
│   ├── RefreshToken.java
│   └── RoleType.java
├── repository/             # Слой доступа к данным
│   ├── UserRepository.java
│   ├── RefreshTokenRepository.java
│   └── impl/
│       └── RefreshTokenRepositoryImpl.java
├── service/                # Слой бизнес-логики
│   ├── UserService.java
│   ├── RefreshTokenService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       └── RefreshTokenServiceImpl.java
├── security/               # Компоненты безопасности
│   ├── SecurityService.java
│   ├── jwt/
│   │   └── TokenService.java
│   ├── SecurityAuthConverter.java
│   └── ReactiveAuthentificationManagerImpl.java
├── web/                    # Web слой
│   ├── controller/
│   └── dto/
└── exception/              # Кастомные исключения
```

## 🎨 Паттерны проектирования

### 1. Repository Pattern
Инкапсулирует логику доступа к данным, предоставляя объектно-ориентированный интерфейс.

**Пример:**
```java
public interface RefreshTokenRepository {
    Mono<Boolean> save(RefreshToken refreshToken, Duration expTime);
    Mono<RefreshToken> getByValue(String refreshToken);
}
```

### 2. Service Layer Pattern
Разделяет бизнес-логику от контроллеров и репозиториев.

**Пример:**
```java
@Service
public class SecurityService {
    public Mono<TokenData> processPasswordToken(String username, String password) {
        return userService.findByUsername(username)
                .flatMap(user -> createTokenData(user));
    }
}
```

### 3. Strategy Pattern
Различные стратегии аутентификации через интерфейсы Spring Security.

**Пример:**
```java
@Component
public class SecurityAuthConverter implements ServerAuthenticationConverter {
    // Стратегия извлечения JWT токена из запроса
}
```

### 4. Adapter Pattern
Адаптация внешних интерфейсов к внутренним.

**Пример:**
```java
public class AppUserDetails implements UserDetails {
    private final User user; // Адаптация User к UserDetails
}
```

### 5. Reactive Programming Pattern
Неблокирующие операции с использованием реактивных типов (Mono/Flux).

**Пример:**
```java
public Mono<TokenData> processPasswordToken(String username, String password) {
    return userService.findByUsername(username)
            .flatMap(user -> {
                // Реактивная цепочка
                return createTokenData(user);
            });
}
```

### 6. DTO Pattern
Объекты для передачи данных между слоями.

**Примеры:**
- `PasswordTokenRequest` - запрос на получение токена
- `RefreshTokenRequest` - запрос на обновление токена
- `TokenResponse` - ответ с токенами

### 7. Dependency Injection
Контейнер Spring IoC для управления зависимостями.

**Пример:**
```java
@Service
@RequiredArgsConstructor  // Конструкторная инъекция через Lombok
public class SecurityService {
    private final UserService userService;
    private final TokenService tokenService;
}
```

### 8. Factory Pattern
Создание объектов через Spring Bean Factory.

**Пример:**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

## 📦 Зависимости

### Основные зависимости

| Зависимость | Версия | Назначение |
|------------|---------|-----------|
| `spring-boot-starter-webflux` | 3.3.4 | Реактивный веб-фреймворк |
| `spring-boot-starter-security` | 3.3.4 | Фреймворк безопасности |
| `spring-boot-starter-data-mongodb-reactive` | 3.3.4 | Реактивный драйвер MongoDB |
| `spring-boot-starter-data-redis-reactive` | 3.3.4 | Реактивный клиент Redis |
| `io.jsonwebtoken:jjwt-api` | 0.11.1 | JWT API |
| `io.jsonwebtoken:jjwt-impl` | 0.11.5 | Реализация JWT |
| `io.jsonwebtoken:jjwt-jackson` | 0.11.5 | Сериализатор JWT Jackson |
| `org.projectlombok:lombok` | - | Генерация кода |

### Тестовые зависимости

| Зависимость | Назначение |
|------------|-----------|
| `spring-boot-starter-webflux-test` | Тестирование WebFlux |
| `spring-boot-starter-security-test` | Тестирование безопасности |
| `testcontainers-redis` | Интеграционные тесты Redis |
| `spring-boot-starter-data-mongodb-reactive-test` | Тестирование MongoDB |

## 📡 API Endpoints

### Публичные endpoints (не требуют аутентификации)

#### Создание пользователя
```http
POST /api/v1/public/user
Content-Type: application/json

{
  "username": "user1",
  "password": "password123",
  "email": "user1@example.com",
  "roles": ["ROLE_USER"]
}
```

#### Получение токена (аутентификация по паролю)
```http
POST /api/v1/public/token/password
Content-Type: application/json

{
  "username": "user1",
  "password": "password123"
}
```

**Ответ:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlkIjoiMTIzIiwicm9sZSI6WyJVU0VSIl0sImlhdCI6MTYzODk2NzIwMCwiZXhwIjoxNjM4OTY3MjYwfQ...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Обновление токена
```http
POST /api/v1/public/token/refresh
Content-Type: application/json

{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Ответ:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "660e8400-e29b-41d4-a716-446655440001"
}
```

### Защищенные endpoints (требуют JWT токен)

#### Получить информацию о пользователе
```http
GET /user
Authorization: Bearer <JWT_TOKEN>
```

**Требует роль:** `ROLE_USER`

#### Административный endpoint
```http
GET /admin
Authorization: Bearer <JWT_TOKEN>
```

**Требует роль:** `ROLE_ADMIN`

#### Менеджерский endpoint
```http
GET /manager
Authorization: Bearer <JWT_TOKEN>
```

**Требует роль:** `ROLE_MANAGER`

## 📮 Работа с Postman

### Настройка окружения

1. **Создайте новое окружение в Postman:**
   - Нажмите на иконку шестеренки (⚙️) в правом верхнем углу
   - Выберите "Add" для создания нового окружения
   - Назовите его "JWT Example Local"

2. **Добавьте переменные окружения:**
   - `base_url` = `http://localhost:8080`
   - `jwt_token` = (оставьте пустым, будет заполнено автоматически)
   - `refresh_token` = (оставьте пустым, будет заполнено автоматически)

### Шаг 1: Создание пользователя

**Создайте новый запрос:**

- **Метод:** `POST`
- **URL:** `{{base_url}}/api/v1/public/user`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "roles": ["ROLE_USER"]
  }
  ```

**Скриншот настроек:**
- Вкладка "Body" → выберите "raw" → выберите "JSON"
- Вставьте JSON выше

**Ожидаемый ответ (200 OK):**
```
User successfully created
```

### Шаг 2: Получение JWT токена

**Создайте новый запрос:**

- **Метод:** `POST`
- **URL:** `{{base_url}}/api/v1/public/token/password`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "username": "testuser",
    "password": "password123"
  }
  ```
  
**Ожидаемый ответ (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlkIjoiNTUwZTg0MDAtZTI5Yi00MWQ0LWE3MTYtNDQ2NjU1NDQwMDAwIiwicm9sZSI6WyJVU0VSIl0sImlhdCI6MTYzODk2NzIwMCwiZXhwIjoxNjM4OTY3MjYwfQ...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Шаг 3: Использование защищенных endpoints

**Создайте новый запрос для получения информации о пользователе:**

- **Метод:** `GET`
- **URL:** `{{base_url}}/user`
- **Headers:**
  ```
  Authorization: Bearer {{jwt_token}}
  ```

**Настройка авторизации в Postman:**

Альтернативный способ - использовать вкладку "Authorization":
1. Выберите тип "Bearer Token"
2. В поле "Token" введите: `{{jwt_token}}`

**Ожидаемый ответ (200 OK):**
```
User
```

### Шаг 4: Обновление токена

**Создайте новый запрос:**

- **Метод:** `POST`
- **URL:** `{{base_url}}/api/v1/public/token/refresh`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "refreshToken": "{{refresh_token}}"
  }
  ```

**Добавьте скрипт в "Tests" для автоматического обновления токенов:**

```javascript
// Проверка успешного ответа
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    
    // Обновление JWT токена
    pm.environment.set("jwt_token", jsonData.token);
    
    // Обновление refresh токена
    pm.environment.set("refresh_token", jsonData.refreshToken);
    
    console.log("Токены обновлены:");
    console.log("New JWT Token:", jsonData.token);
    console.log("New Refresh Token:", jsonData.refreshToken);
}
```

**Ожидаемый ответ (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlkIjoiNjYwZTg0MDAtZTI5Yi00MWQ0LWE3MTYtNDQ2NjU1NDQwMDAxIiwicm9sZSI6WyJVU0VSIl0sImlhdCI6MTYzODk2NzMwMCwiZXhwIjoxNjM4OTY3MzYwfQ...",
  "refreshToken": "660e8400-e29b-41d4-a716-446655440001"
}
```

### Шаг 5: Тестирование ролей

**Создайте пользователя с ролью ADMIN:**

1. **Создание администратора:**
   - **Метод:** `POST`
   - **URL:** `{{base_url}}/api/v1/public/user`
   - **Body:**
     ```json
     {
       "username": "admin",
       "password": "admin123",
       "email": "admin@example.com",
       "roles": ["ROLE_ADMIN"]
     }
     ```

2. **Получение токена администратора:**
   - Используйте запрос из Шага 2 с учетными данными администратора
   - Сохраните токен в переменную `admin_jwt_token`

3. **Тестирование административного endpoint:**
   - **Метод:** `GET`
   - **URL:** `{{base_url}}/admin`
   - **Headers:**
     ```
     Authorization: Bearer {{admin_jwt_token}}
     ```

## 🔐 Безопасность

### JWT Токен

- **Алгоритм:** HS512 (HMAC SHA-512)
- **Срок жизни:** 1 минута (настраивается)
- **Claims:**
  - `sub` - username
  - `id` - user ID
  - `role` - список ролей
  - `iat` - время создания
  - `exp` - время истечения

### Refresh Токен

- **Формат:** UUID
- **Хранение:** Redis с TTL
- **Срок жизни:** 20 секунд (настраивается)
- **Особенности:**
  - Одноразовое использование (удаляется после использования)
  - Индексируется в Redis Hash для быстрого поиска

### Безопасность паролей

- **Хеширование:** BCrypt с strength 12
- **Хранение:** Только хеш хранится в MongoDB

## ⚙️ Конфигурация

### application.yml

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
    mongodb:
      uri: mongodb://root:root@localhost:27017/users_db?authSource=admin
      auto-index-creation: true

user-service:
  jwt:
    secret: <минимум 64 символа для HS512>
    tokenExpiration: 1m
    refreshTokenExpiration: 20s
```

### Переменные окружения

Вы можете переопределить конфигурацию, используя переменные окружения:

```bash
export SPRING_DATA_MONGODB_URI="mongodb://user:pass@host:27017/db"
export SPRING_DATA_REDIS_HOST="localhost"
export SPRING_DATA_REDIS_PORT="6379"
export USER_SERVICE_JWT_SECRET="ваш-секретный-ключ"
```

## 🧪 Тестирование

### Запуск тестов

```bash
# Запустить все тесты
./gradlew test

# Запустить с покрытием (если настроено)
./gradlew test jacocoTestReport
```

### Структура тестов

- Модульные тесты для сервисов
- Интеграционные тесты с Testcontainers
- Тесты безопасности с mock аутентификацией

## 📚 Дополнительная информация

### Роли пользователей

- `ROLE_USER` - Обычный пользователь
- `ROLE_ADMIN` - Администратор
- `ROLE_MANAGER` - Менеджер

### Особенности реализации

1. **Реактивная архитектура** - Все операции неблокирующие
2. **Одноразовые refresh токены** - Токены удаляются после использования
3. **Hash индексация в Redis** - Быстрый поиск refresh токенов
4. **Безопасность на уровне методов** - `@PreAuthorize` для проверки ролей
5. **Кастомные исключения** - Специализированные исключения для разных сценариев

## 🤝 Вклад в проект

Этот проект создан в образовательных целях для демонстрации:
- Реактивного программирования с Spring WebFlux
- JWT аутентификации
- Интеграции MongoDB и Redis
- Паттернов проектирования в Spring приложениях

Вклад в проект, вопросы и запросы на новые функции приветствуются!

## 📄 Лицензия

Этот проект создан в образовательных целях.

---

<div align="center">

[⬆ Наверх](#-пример-jwt-аутентификации)

</div>
