package com.haojie.secret_santa.controller.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.haojie.secret_santa.dto.BookGiftRequest;
import com.haojie.secret_santa.dto.GiftDTO;
import com.haojie.secret_santa.dto.GiftRequest;
import com.haojie.secret_santa.dto.WishlistDTO;
import com.haojie.secret_santa.dto.WishlistRequest;
import com.haojie.secret_santa.model.auth.User;
import com.haojie.secret_santa.model.entity.Gift;
import com.haojie.secret_santa.model.entity.Wishlist;
import com.haojie.secret_santa.repository.auth.UserRepository;
import com.haojie.secret_santa.service.GiftService;
import com.haojie.secret_santa.service.WishlistService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private UserRepository userRepository;

    // - Wishlists
    // - **GET** `/api/wishlists` - return user's all wishlists
    @GetMapping
    public ResponseEntity<List<WishlistDTO>> getMyWishlists() {
        User currentUser = getCurrentUser();
        List<Wishlist> wishlists = currentUser.getWishlists();
        List<WishlistDTO> dtos = wishlists.stream()
                .map(this::toWishlistDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // - **POST** `/api/wishlists` - create a new wishlist for the user
    @PostMapping
    public ResponseEntity<WishlistDTO> createWishlist(@Valid @RequestBody WishlistRequest request) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = new Wishlist();
        wishlist.setName(request.getName());
        wishlist.setOwner(currentUser);
        wishlist.setPublic(false);

        Wishlist saved = wishlistService.store(wishlist);
        return ResponseEntity.status(HttpStatus.CREATED).body(toWishlistDTO(saved));
    }

    // - **GET** `/api/wishlists/{wishlistId}` - return wishlist details
    // - If the user is not the owner of the wishlist and the wishlist is not
    // public, return 403 Forbidden
    @GetMapping("/{wishlistId}")
    public ResponseEntity<WishlistDTO> getWishlist(@PathVariable Integer wishlistId) {
        Wishlist wishlist = wishlistService.getById(wishlistId);
        if (wishlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }

        User currentUser = getCurrentUser();
        boolean isOwner = currentUser != null && wishlist.getOwner().getId().equals(currentUser.getId());

        if (!isOwner && !wishlist.isPublic()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this wishlist");
        }

        return ResponseEntity.ok(toWishlistDTO(wishlist));
    }

    // - **PUT** `/api/wishlists/{wishlistId}` - update a wishlist
    @PutMapping("/{wishlistId}")
    public ResponseEntity<WishlistDTO> updateWishlist(@PathVariable Integer wishlistId,
            @Valid @RequestBody WishlistRequest request) {
        Wishlist wishlist = wishlistService.getById(wishlistId);
        if (wishlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }

        User currentUser = getCurrentUser();
        if (!wishlist.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this wishlist");
        }

        if (wishlist.isPublic()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update a published wishlist");
        }

        wishlist.setName(request.getName());
        Wishlist updated = wishlistService.update(wishlist);
        return ResponseEntity.ok(toWishlistDTO(updated));
    }

    // - **DELETE** `/api/wishlists/{wishlistId}` - delete a wishlist (only if owner
    // and not published)
    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Integer wishlistId) {
        Wishlist wishlist = wishlistService.getById(wishlistId);
        if (wishlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }

        User currentUser = getCurrentUser();
        if (!wishlist.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this wishlist");
        }

        if (wishlist.isPublic()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete a published wishlist");
        }

        wishlistService.delete(wishlist);
        return ResponseEntity.noContent().build();
    }

    // - **POST** `/api/wishlists/{wishlistId}/publish` - publish a wishlist (only
    // if owner and not published)
    @PostMapping("/{wishlistId}/publish")
    public ResponseEntity<WishlistDTO> publishWishlist(@PathVariable Integer wishlistId) {
        Wishlist wishlist = wishlistService.getById(wishlistId);
        if (wishlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }

        User currentUser = getCurrentUser();
        if (!wishlist.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to publish this wishlist");
        }

        if (wishlist.isPublic()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wishlist is already published");
        }

        wishlist.setPublic(true);
        Wishlist updated = wishlistService.update(wishlist);
        return ResponseEntity.ok(toWishlistDTO(updated));
    }

    // - **GET** `/api/wishlists/{wishlistId}/gift/{giftId}` - restituisce i
    // dettagli di un regalo (se proprietario della wishlist o se la wishlist è
    // pubblica)
    @GetMapping("/{wishlistId}/gift/{giftId}")
    public ResponseEntity<GiftDTO> getGift(@PathVariable Integer wishlistId, @PathVariable Integer giftId) {
        Gift gift = giftService.getById(giftId);
        if (gift == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gift not found");
        }

        if (!gift.getWishlist().getId().equals(wishlistId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gift does not belong to the specified wishlist");
        }

        Wishlist wishlist = gift.getWishlist();
        User currentUser = getCurrentUser();
        boolean isOwner = currentUser != null && wishlist.getOwner().getId().equals(currentUser.getId());

        if (!isOwner && !wishlist.isPublic()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this gift");
        }

        return ResponseEntity.ok(toGiftDTO(gift));
    }

    // - **POST** `/api/wishlists/{wishlistId}/gift` - aggiunge un nuovo regalo alla
    // wishlist (solo se proprietario e non pubblicata)
    @PostMapping("/{wishlistId}/gift")
    public ResponseEntity<GiftDTO> addGift(@PathVariable Integer wishlistId, @Valid @RequestBody GiftRequest request) {
        Wishlist wishlist = wishlistService.getById(wishlistId);
        if (wishlist == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }

        User currentUser = getCurrentUser();
        if (!wishlist.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to add gifts to this wishlist");
        }

        if (wishlist.isPublic()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add gifts to a published wishlist");
        }

        Gift gift = new Gift();
        gift.setName(request.getName());
        gift.setImageUrl(request.getImageUrl());
        gift.setLinkUrl(request.getLinkUrl());
        gift.setPriceInCents(request.getPriceInCents());
        gift.setPriority(request.getPriority());
        gift.setNote(request.getNote());
        gift.setWishlist(wishlist);
        gift.setBooked(false);

        Gift saved = giftService.store(gift);
        return ResponseEntity.status(HttpStatus.CREATED).body(toGiftDTO(saved));
    }

    // - **PUT** `/api/wishlists/{wishlistId}/gift/{giftId}` - aggiorna un regalo
    // della wishlist (solo se proprietario e non pubblicata)
    @PutMapping("/{wishlistId}/gift/{giftId}")
    public ResponseEntity<GiftDTO> updateGift(@PathVariable Integer wishlistId, @PathVariable Integer giftId,
            @Valid @RequestBody GiftRequest request) {
        Gift gift = giftService.getById(giftId);
        if (gift == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gift not found");
        }

        if (!gift.getWishlist().getId().equals(wishlistId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gift does not belong to the specified wishlist");
        }

        Wishlist wishlist = gift.getWishlist();
        User currentUser = getCurrentUser();
        if (!wishlist.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to update gifts in this wishlist");
        }

        if (wishlist.isPublic()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update gifts in a published wishlist");
        }

        gift.setName(request.getName());
        gift.setImageUrl(request.getImageUrl());
        gift.setLinkUrl(request.getLinkUrl());
        gift.setPriceInCents(request.getPriceInCents());
        gift.setPriority(request.getPriority());
        gift.setNote(request.getNote());

        Gift updated = giftService.update(gift);
        return ResponseEntity.ok(toGiftDTO(updated));
    }

    // - **DELETE** `/api/wishlists/{wishlistId}/gift/{giftId}` - elimina un regalo
    // dalla wishlist (solo se proprietario e non pubblicata)
    @DeleteMapping("/{wishlistId}/gift/{giftId}")
    public ResponseEntity<Void> deleteGift(@PathVariable Integer wishlistId, @PathVariable Integer giftId) {
        Gift gift = giftService.getById(giftId);
        if (gift == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gift not found");
        }

        if (!gift.getWishlist().getId().equals(wishlistId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gift does not belong to the specified wishlist");
        }

        Wishlist wishlist = gift.getWishlist();
        User currentUser = getCurrentUser();
        if (!wishlist.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to delete gifts from this wishlist");
        }

        if (wishlist.isPublic()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete gifts from a published wishlist");
        }

        giftService.delete(gift);
        return ResponseEntity.noContent().build();
    }

    // - **POST** `/api/wishlists/{wishlistId}/gift/{giftId}/book` - prenota un
    // regalo della wishlist (se la wishlist è pubblica)
    // - JSON richiesta { "message": "string" (opzionale) }
    // - Controlli: isPublished == false || isBooked == true -> fail prenotazione
    @PostMapping("/{wishlistId}/gift/{giftId}/book")
    public ResponseEntity<GiftDTO> bookGift(@PathVariable Integer wishlistId, @PathVariable Integer giftId,
            @RequestBody BookGiftRequest request) {
        Gift gift = giftService.getById(giftId);
        if (gift == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gift not found");
        }

        if (!gift.getWishlist().getId().equals(wishlistId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gift does not belong to the specified wishlist");
        }

        Wishlist wishlist = gift.getWishlist();
        if (!wishlist.isPublic()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot book a gift from a non-public wishlist");
        }

        if (gift.isBooked()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gift is already booked");
        }

        // Race condition handling not implemented

        User currentUser = getCurrentUser();
        // ** TODO: Can be null if public access allows anonymous booking? */

        gift.setBooked(true);
        gift.setMessage(request.getMessage());
        if (currentUser != null) {
            gift.setUser(currentUser);
        }

        Gift updated = giftService.update(gift);
        return ResponseEntity.ok(toGiftDTO(updated));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        // If using JWT, the principal might be UserDetailsImpl or just username string
        // depending on filter setup.
        // Based on AuthServiceImpl, it sets UserDetailsImpl.
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByUsername(username).orElse(null);
    }

    private WishlistDTO toWishlistDTO(Wishlist wishlist) {
        List<GiftDTO> giftDTOs = null;
        if (wishlist.getGifts() != null) {
            giftDTOs = wishlist.getGifts().stream()
                    .map(this::toGiftDTO)
                    .collect(Collectors.toList());
        }
        return new WishlistDTO(
                wishlist.getId(),
                wishlist.getName(),
                wishlist.getOwner().getUsername(),
                wishlist.isPublic(),
                giftDTOs);
    }

    private GiftDTO toGiftDTO(Gift gift) {
        return new GiftDTO(
                gift.getId(),
                gift.getName(),
                gift.getImageUrl(),
                gift.getLinkUrl(),
                gift.getPriceInCents(),
                gift.getPriority(),
                gift.getNote(),
                gift.isBooked(),
                gift.getMessage());
    }
}
