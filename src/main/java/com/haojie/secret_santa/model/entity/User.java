package com.haojie.secret_santa.model.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Username cannot be empty")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Column(nullable = false, unique = true)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password; // salvare HASHED

    @OneToMany(mappedBy = "owner")
    @JsonBackReference
    private List<Wishlist> wishlists;

    @ManyToMany(mappedBy = "savedByUsers")
    private List<Wishlist> savedWishlists;

    @OneToMany(mappedBy = "user")
    private List<Gift> bookedGifts;

    // @ManyToMany(fetch = FetchType.EAGER)
    // @JoinTable(name = "role_user", joinColumns = @JoinColumn(name = "user_id"),
    // inverseJoinColumns = @JoinColumn(name = "role_id"))
    // private Set<Role> roles;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // getters and setters
}