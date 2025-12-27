package com.haojie.secret_santa.model.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "gifts")
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String imageUrl;

    @NotBlank
    private String linkUrl;

    @NotBlank
    @Min(0)
    private long priceInCents;

    @NotBlank
    @Min(0)
    @Max(5)
    private int priority;

    @Lob
    private String note;

    @ManyToOne
    // @JoinColumn(name = "wishlist_id")
    private Wishlist wishlist;

    @ManyToOne
    // @JoinColumn(name = "user_id")
    private User user;

    @Lob
    private String message;

    private boolean isBooked;

    // Constructors
    public Gift() {
    }

    public Gift(String name, String imageUrl, String linkUrl, long priceInCents, int priority, String note,
            Wishlist wishlist) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.priceInCents = priceInCents;
        this.priority = priority;
        this.note = note;
        this.wishlist = wishlist;
        this.isBooked = false;
    }

    // Getters and Setters

}
