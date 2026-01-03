package com.haojie.secret_santa.model.payload.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email not valid")
    private String email;

    @NotBlank(message = "password must not be blank")
    @Size(min = 6, message = "passowrd must be at least 6 characters")
    private String password;

    @NotBlank(message = "username must not be blank")
    private String username;

    @Column(nullable = false)
    private String role;

    public RegisterRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
