package com.coworkflex.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;

    public User() {}

    public User(Long id, String fullName, String email, String password,
                Role role, LocalDateTime createdAt, List<Reservation> reservations) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.reservations = reservations;
    }

    // ─── Getters / Setters ─────────────────────────────────────
    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public String getFullName()                { return fullName; }
    public void setFullName(String fullName)   { this.fullName = fullName; }
    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }
    public void setPassword(String password)   { this.password = password; }
    public Role getRole()                      { return role; }
    public void setRole(Role role)             { this.role = role; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> r) { this.reservations = r; }

    // ─── UserDetails ───────────────────────────────────────────
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getPassword()             { return password; }
    @Override public String getUsername()             { return email; }
    @Override public boolean isAccountNonExpired()    { return true; }
    @Override public boolean isAccountNonLocked()     { return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled()              { return true; }

    // ─── Builder ───────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String fullName;
        private String email;
        private String password;
        private Role role = Role.USER;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Builder id(Long id)                  { this.id = id; return this; }
        public Builder fullName(String v)           { this.fullName = v; return this; }
        public Builder email(String v)              { this.email = v; return this; }
        public Builder password(String v)           { this.password = v; return this; }
        public Builder role(Role v)                 { this.role = v; return this; }
        public Builder createdAt(LocalDateTime v)   { this.createdAt = v; return this; }

        public User build() {
            User u = new User();
            u.id = id; u.fullName = fullName; u.email = email;
            u.password = password; u.role = role; u.createdAt = createdAt;
            return u;
        }
    }

    public enum Role { USER, ADMIN }
}
