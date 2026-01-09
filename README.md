# KOMUNA Mini

**KOMUNA Mini** – tai paprasta daugiabučių bendrijų valdymo informacinė sistema, sukurta naudojant **Spring Boot** ir **Thymeleaf**. Sistema skirta bendrijų administravimui, paslaugų ir kainų valdymui bei gyventojų informavimui pagal naudotojų roles.

---

## Naudotos technologijos

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- H2 / SQL duomenų bazė
- HTML, CSS, JavaScript
- Maven

---

## Naudotojų rolės

### ADMIN
- Kuria ir redaguoja bendrijas
- Kuria ir valdo vartotojus
- Kuria paslaugas (Fee) ir kainas (Price)
- Valdo visą sistemą

### MANAGER
- Valdo tik savo priskirtą bendriją
- Kuria ir redaguoja vartotojus savo bendrijoje
- Kuria paslaugas ir kainas savo bendrijai

### RESIDENT
- Peržiūri savo bendrijos paslaugas ir kainas
- Redaguoja savo profilį (vardą, el. paštą, slaptažodį)

---

## Sistemos struktūra

Projektas organizuotas pagal sluoksninę architektūrą:

- `controller` – HTTP užklausų valdymas
- `service` – verslo logika
- `repository` – duomenų prieiga (JPA)
- `model` – domeno objektai (Entity)
- `templates` – Thymeleaf šablonai
- `static` – CSS ir JavaScript failai

---

## Saugumas

- Autentifikacija ir autorizacija įgyvendinta su **Spring Security**
- Prieigos teisės valdomos pagal roles ir URL
- CSRF apsauga naudojama formų užklausoms

---

## Paleidimas

1. Atidaryti projektą **IntelliJ IDEA**
2. Paleisti klasę `KomunaMiniApplication`
3. Naršyklėje atidaryti:  
   👉 `http://localhost:8080`

---

## Pastabos

Projektas sukurtas mokymosi tikslais ir atitinka objektinio programavimo principus:
- SOLID
- Paveldėjimą
- Polimorfizmą
- Enkapsuliaciją

---

