package com.haojie.secret_santa.model.dto;

public class GiftDTO {
    private Integer id;
    private String name;
    private String imageUrl;
    private String linkUrl;
    private long priceInCents;
    private int priority;
    private String note;
    private boolean isBooked;
    private String message;

    public GiftDTO() {
    }

    public GiftDTO(Integer id, String name, String imageUrl, String linkUrl, long priceInCents, int priority,
            String note, boolean isBooked, String message) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.priceInCents = priceInCents;
        this.priority = priority;
        this.note = note;
        this.isBooked = isBooked;
        this.message = message;
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

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
