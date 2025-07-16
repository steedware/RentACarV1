# RentACar - System Wypożyczania Samochodów z Geolokalizacją

## Przegląd

RentACar to kompleksowy system wypożyczania samochodów oparty na sieci, który umożliwia użytkownikom znajdowanie, rezerwowanie i wynajmowanie pojazdów z funkcjami śledzenia geolokalizacji w czasie rzeczywistym. Aplikacja zapewnia intuicyjny interfejs dla klientów do wyszukiwania pojazdów na podstawie lokalizacji, typu i dostępności, a także oferuje solidny panel administracyjny do zarządzania flotą.

## Funkcje

### Funkcje dla klientów
- Rejestracja i uwierzytelnianie użytkowników
- Wyszukiwanie pojazdów według lokalizacji, typu i dostępności
- Wyświetlanie pojazdów na interaktywnych mapach
- Dokonywanie rezerwacji z płatnościami online
- Śledzenie historii i statusu wynajmu
- Ocena i recenzowanie wynajmów pojazdów

### Funkcje dla administratorów
- Zarządzanie flotą (dodawanie, edytowanie, usuwanie pojazdów)
- Zarządzanie użytkownikami
- Nadzór nad rezerwacjami
- Analizy i raporty
- Śledzenie geolokalizacji floty

## Stos technologiczny

### Backend
- Java 17
- Spring Boot 3.1.5
- Spring Security
- Spring Data JPA
- Hibernate Spatial do geolokalizacji

### Frontend
- Szablony Thymeleaf
- Bootstrap 5
- Leaflet.js do map
- JavaScript

### Baza danych
- PostgreSQL z rozszerzeniem PostGIS do funkcji geograficznych

### Integracje zewnętrzne
- Stripe do przetwarzania płatności
- OpenStreetMap do geolokalizacji

## Instrukcja instalacji

### Wymagania wstępne
- Java 17 lub nowsza
- Maven
- PostgreSQL z rozszerzeniem PostGIS
- Konto Stripe do przetwarzania płatności

### Konfiguracja bazy danych
1. Utwórz bazę danych PostgreSQL o nazwie `rentacar`
2. Włącz rozszerzenie PostGIS wykonując: `CREATE EXTENSION postgis;`

### Konfiguracja aplikacji
1. Sklonuj repozytorium
2. Skonfiguruj połączenie z bazą danych w pliku `application.properties`
3. Ustaw klucze API Stripe w pliku `application.properties`
4. Zbuduj projekt za pomocą Mavena: `mvn clean install`
5. Uruchom aplikację: `mvn spring-boot:run`
6. Dostęp do aplikacji pod adresem: `http://localhost:8080`

### Początkowe konto administratora
Podczas pierwszego uruchomienia tworzone jest konto administratora z następującymi danymi:
- Email: admin@rentacar.com
- Hasło: admin

(Pamiętaj, aby zmienić te dane logowania w środowisku produkcyjnym)

## Struktura projektu

- `src/main/java/com/rentacar/controller/` - Kontrolery webowe
- `src/main/java/com/rentacar/model/` - Modele encji
- `src/main/java/com/rentacar/repository/` - Repozytoria danych
- `src/main/java/com/rentacar/service/` - Usługi logiki biznesowej
- `src/main/java/com/rentacar/config/` - Klasy konfiguracyjne
- `src/main/resources/templates/` - Szablony Thymeleaf
- `src/main/resources/static/` - Zasoby statyczne (CSS, JS, obrazy)

## Dokumentacja API

Aplikacja udostępnia kilka punktów końcowych RESTful do geolokalizacji pojazdów i płatności:

- `GET /api/vehicles/public/all` - Pobierz wszystkie dostępne pojazdy
- `GET /api/vehicles/public/nearby` - Pobierz pojazdy w pobliżu współrzędnych
- `PUT /api/vehicles/{id}/location` - Zaktualizuj lokalizację pojazdu

## Licencja

Ten projekt jest licencjonowany na podstawie licencji MIT - zobacz plik LICENSE, aby uzyskać szczegółowe informacje.
