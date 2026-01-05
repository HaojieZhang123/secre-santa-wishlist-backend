package com.haojie.secret_santa.dto;

public class BookGiftRequest {
    private String message;

    public BookGiftRequest() {
    }

    public BookGiftRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
