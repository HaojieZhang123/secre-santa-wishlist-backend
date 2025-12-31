package com.haojie.secret_santa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.haojie.secret_santa.model.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

}
