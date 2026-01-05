package com.haojie.secret_santa.model.dto;

import java.util.List;

public class WishlistDTO {
    private Integer id;
    private String name;
    private String ownerUsername;
    private boolean isPublic;
    private List<GiftDTO> gifts;

    public WishlistDTO() {
    }

    public WishlistDTO(Integer id, String name, String ownerUsername, boolean isPublic, List<GiftDTO> gifts) {
        this.id = id;
        this.name = name;
        this.ownerUsername = ownerUsername;
        this.isPublic = isPublic;
        this.gifts = gifts;
    }

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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public List<GiftDTO> getGifts() {
        return gifts;
    }

    public void setGifts(List<GiftDTO> gifts) {
        this.gifts = gifts;
    }
}
