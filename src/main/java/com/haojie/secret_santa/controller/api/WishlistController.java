package com.haojie.secret_santa.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {
    // - Wishlists
    // - **GET** `/api/wishlists` - restituisce tutte le wishlist dell'utente
    // loggato

    // - **POST** `/api/wishlists` - crea una nuova wishlist per l'utente loggato

    // - **GET** `/api/wishlists/{wishlistId}` - restituisce i dettagli di una
    // wishlist (se proprietario o se pubblicata)
    // - Se non sono il proprietario della wishlist e wishlist non e' pubblicata,
    // restituisco 403 Forbidden

    // - **PUT** `/api/wishlists/{wishlistId}` - aggiorna una wishlist (solo se
    // proprietario e non pubblicata)

    // - **DELETE** `/api/wishlists/{wishlistId}` - elimina una wishlist (solo se
    // proprietario e non pubblicata)

    // - **POST** `/api/wishlists/{wishlistId}/publish` - pubblica una wishlist
    // (solo se proprietario e non pubblicata)

    // - **GET** `/api/wishlists/{wishlistId}/gift/{giftId}` - restituisce i
    // dettagli di un regalo (se proprietario della wishlist o se la wishlist è
    // pubblica)

    // - **POST** `/api/wishlists/{wishlistId}/gift` - aggiunge un nuovo regalo alla
    // wishlist (solo se proprietario e non pubblicata)

    // - **PUT** `/api/wishlists/{wishlistId}/gift/{giftId}` - aggiorna un regalo
    // della wishlist (solo se proprietario e non pubblicata)

    // - **DELETE** `/api/wishlists/{wishlistId}/gift/{giftId}` - elimina un regalo
    // dalla wishlist (solo se proprietario e non pubblicata)

    // - **POST** `/api/wishlists/{wishlistId}/gift/{giftId}/book` - prenota un
    // regalo della wishlist (se la wishlist è pubblica)
    // - JSON richiesta {
    // "message": "string" (opzionale)
    // }
    // - Controlli:
    // - isPublished == false || isBooked == true -> fail prenotazione (race
    // condition handling?)

}
