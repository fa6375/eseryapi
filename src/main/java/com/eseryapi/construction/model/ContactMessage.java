package com.eseryapi.construction.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Zəhmət olmasa adınızı bildirin.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Zəhmət olmasa e-poçt ünvanınızı daxil edin.")
    @Email(message = "Zəhmət olmasa düzgün e-poçt ünvanı daxil edin.")
    @Column(nullable = false)
    private String email;

    private String phone;

    @NotBlank(message = "Zəhmət olmasa layihəniz haqqında bir neçə detal əlavə edin.")
    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean isRead = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
