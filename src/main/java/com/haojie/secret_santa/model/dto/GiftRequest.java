package com.haojie.secret_santa.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class GiftRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String imageUrl;
    @NotBlank
    private String linkUrl;
    @Min(0)
    private long priceInCents;
    @Min(1)
    @Max(5)
    private int priority;
    private String note;

    public GiftRequest() {
    }

    public GiftRequest(String name, String imageUrl, String linkUrl, long priceInCents, int priority, String note) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.priceInCents = priceInCents;
        this.priority = priority;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public long getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(long priceInCents) {
        this.priceInCents = priceInCents;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
