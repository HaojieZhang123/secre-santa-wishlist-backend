package com.haojie.secret_santa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haojie.secret_santa.model.entity.Wishlist;
import com.haojie.secret_santa.repository.WishlistRepository;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    public List<Wishlist> findAll() {
        return wishlistRepository.findAll();
    }

    public Optional<Wishlist> findById(Integer id) {
        return wishlistRepository.findById(id);
    }

    public Wishlist getById(Integer id) {
        Optional<Wishlist> wishlist = wishlistRepository.findById(id);
        if (wishlist.isEmpty()) {
            return null;
        } else {
            return wishlist.get();
        }
    }

    public Wishlist store(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    public Wishlist update(Wishlist wishlist) {
        return wishlistRepository.save(wishlist);
    }

    public void deleteById(Integer id) {
        wishlistRepository.deleteById(id);
    }

    public void delete(Wishlist wishlist) {
        wishlistRepository.delete(wishlist);
    }
}
