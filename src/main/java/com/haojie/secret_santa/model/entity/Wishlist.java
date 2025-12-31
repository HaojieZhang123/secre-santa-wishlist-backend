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
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getSavedByUsers() {
        return savedByUsers;
    }

    public void setSavedByUsers(List<User> savedByUsers) {
        this.savedByUsers = savedByUsers;
    }

    public List<Gift> getGifts() {
        return gifts;
    }

    public void setGifts(List<Gift> gifts) {
        this.gifts = gifts;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

}
