package com.haojie.secret_santa.model.dto;

import jakarta.validation.constraints.NotBlank;

public class WishlistRequest {
    @NotBlank
    private String name;

    public WishlistRequest() {
    }

    public WishlistRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
