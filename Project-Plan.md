# Secret Santa Wishlist - Project Plan

# Schemas

## Regalo
- id
- name (string)
- imageUrl (string - URL)
- link (string - URL)
- price (bigint o long e salvare prezzo in centesimi per evitare problemi di arrotondamento)
- priority (integer - 1-5) @Min(1) @Max(5)
- note (string - opzionale) @Lob
- wishlist (Wishlist) @ManyToOne // wishlist a cui appartiene il regalo

- user (ManyToOne) - utente che ha prenotato il regalo (null se non prenotato)
- message (string - opzionale) @Lob - messaggio lasciato da chi ha prenotato
- isBooked (boolean) - indica se il regalo è stato prenotato (default: false | if(!user) false | if(user) true)

## Wishlist
- id (UUID) @GeneratedValue(strategy = GenerationType.UUID)
- name (string)
- owner (User) @ManyToOne
- gifts (List<Regalo>) @OneToMany(mappedBy="wishlist", cascade=CascadeType.ALL, orphanRemoval=true)
- isPublished (boolean) - indica se la wishlist è stata pubblicata (default: false)
- savedByUsers (List<User>) @ManyToMany(mappedBy="savedWishlists") // utenti che hanno salvato la wishlist (opzionale, per funzionalità bonus)

## User
- username 
- passowrd
- wishlists (List<Wishlist>) @OneToMany(mappedBy="owner") // wishlist create dall'utente
- savedWishlists (List<Wishlist>) @ManyToMany // wishlist salvate dall'utente (opzionale, per funzionalità bonus)
- bookedGifts (List<Regalo>) @OneToMany(mappedBy="user") // regali prenotati dall'utente

Usare Spring Security per la gestione dell'autenticazione e autorizzazione.
JWT per la gestione dei token di accesso + refresh token.

# Features

## Frontend

SPA React con nagivazione tramite react Router e Axios per le chiamate API al backend.
- Salvataggio bozza wishlist in localStorage
- Mobile first design
- Client side validation dei form

### Pagine
- Homepage 
    - Lista dei wishlist dell'utente loggato
    - Pulsante per creare nuova wishlist
    - Ad ogni wishlist:
        - Pulsante per modificare (se non pubblicata)
        - Pulsante per pubblicare (se non pubblicata)
        - Pulsante per eliminare (se non pubblicata)
        - Pulsante per visualizzare (se pubblicata)
    - Se utente non loggato:
        - Messaggio che invita a loggarsi o registrarsi per creare wishlist
    - Se nessuna wishlist:
        - Messaggio "Nessuna wishlist presente. Crea la tua prima wishlist!"
- Wishlist details:
    - Wishlist personale non pubblicata
        - Pulsante per aggiungere nuovo regalo
        - Lista dei regali
            - pulsante per modificare ogni regalo
            - pulsante per eliminare ogni regalo
            - Se nessun regalo, mostra messaggio "Nessun regalo presente. Aggiungi il tuo primo regalo!"
        - Pulsante per pubblicare wishlist
        - Pulsante per eliminare wishlist (Modale di conferma + redirect a homepage)
    - Wishlist pubblica
        - List owner's name o "Wishlist di [nome utente]"
        - Lista dei regali
            - Ogni regalo ha un pulsante per vedere i dettagli del regalo
            - Ogni regalo ha un pulsante per prenotare il regalo (campo di testo a comparsa per messaggio opzionale + modale di conferma della prenotazione)
            - Ogni regalo pronotato mostra un flag "Prenotato" ed eventualmente il messaggio associato

- Header component:
    - NavLink a homepage
    - NavLink a lista di wishlist salvate (se implementato bonus)
    - Side button:
        - Pulsante di login (se utente non loggato) / logout (se utente loggato)
        - Pulsante per cambiare tema (light/dark mode)

## Backend
Spring Boot + Spring Security + JPA + MySQL Database + JWT (access token + refresh token) + Thymeleaf (backoffice opzionale bonus)

Backoffice per la visualizzazione e amministrazione delle wishlist e regali da un admin. Diverso da frontend in React che sara' dedicato agli utenti finali.

Backoffice ha security filter separato per un autenticazione session based.
Frontend React ha security filter per JWT token based authentication.

Usa DTO per data transfer tra frontend e backend nell'API.

Implementazione base di JWT authentication con access token e refresh token.

### API Endpoints
- Auth
    - **POST** `/api/auth/register` - registra un nuovo utente
    - **POST** `/api/auth/login` - effettua il login e restituisce access token + refresh token
    - **GET** `/api/auth/logout` - effettua il logout invalidando il refresh token
    - **POST** `/api/auth/refresh-token` - rinnova l'access token usando il refresh token

- Api
    - Wishlists
        - **GET** `/api/wishlists` - restituisce tutte le wishlist dell'utente loggato
        - **POST** `/api/wishlists` - crea una nuova wishlist per l'utente loggato
        - **GET** `/api/wishlists/{wishlistId}` - restituisce i dettagli di una wishlist (se proprietario o se pubblicata)
            - Se non sono il proprietario della wishlist e wishlist non e' pubblicata, restituisco 403 Forbidden
        - **PUT** `/api/wishlists/{wishlistId}` - aggiorna una wishlist (solo se proprietario e non pubblicata)
        - **DELETE** `/api/wishlists/{wishlistId}` - elimina una wishlist (solo se proprietario e non pubblicata)
        - **POST** `/api/wishlists/{wishlistId}/publish` - pubblica una wishlist (solo se proprietario e non pubblicata)
        - **GET** `/api/wishlists/{wishlistId}/gift/{giftId}` - restituisce i dettagli di un regalo (se proprietario della wishlist o se la wishlist è pubblica)
        - **POST** `/api/wishlists/{wishlistId}/gift` - aggiunge un nuovo regalo alla wishlist (solo se proprietario e non pubblicata)
        - **PUT** `/api/wishlists/{wishlistId}/gift/{giftId}` - aggiorna un regalo della wishlist (solo se proprietario e non pubblicata)
        - **DELETE** `/api/wishlists/{wishlistId}/gift/{giftId}` - elimina un regalo dalla wishlist (solo se proprietario e non pubblicata)
        - **POST** `/api/wishlists/{wishlistId}/gift/{giftId}/book` - prenota un regalo della wishlist (se la wishlist è pubblica)
            - JSON richiesta {
                "message": "string" (opzionale)
            }
            - Controlli:
                - isPublished == false || isBooked == true -> fail prenotazione (race condition handling?)

Avere un metodo per comparare utente loggato con proprietario della wishlist per autorizzare le operazioni di modifica/eliminazione/pubblicazione.

