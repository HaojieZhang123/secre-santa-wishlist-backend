package com.haojie.secret_santa.model.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "wishlists")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Integer id;

    @NotBlank
    private String name;

    @ManyToOne
    private User owner;

    @ManyToMany(mappedBy = "savedWishlists")
    private List<User> savedByUsers;

    @OneToMany(mappedBy = "wishlist")
    private List<Gift> gifts;

    private boolean isPublic;

    // Constructors
    public Wishlist() {
    }

    public Wishlist(String name, User owner, boolean isPublic) {
        this.name = name;
        this.owner = owner;
        this.isPublic = isPublic;
    }

    // Getters and Setters

}
