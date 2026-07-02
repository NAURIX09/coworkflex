package com.coworkflex.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desk_id", nullable = false)
    private Desk desk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Reservation() {}

    // ─── Getters / Setters ─────────────────────────────────────
    public Long getId()                             { return id; }
    public void setId(Long id)                      { this.id = id; }
    public LocalDateTime getStartDateTime()         { return startDateTime; }
    public void setStartDateTime(LocalDateTime v)   { this.startDateTime = v; }
    public LocalDateTime getEndDateTime()           { return endDateTime; }
    public void setEndDateTime(LocalDateTime v)     { this.endDateTime = v; }
    public ReservationStatus getStatus()            { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public BigDecimal getTotalPrice()               { return totalPrice; }
    public void setTotalPrice(BigDecimal v)         { this.totalPrice = v; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
    public String getNotes()                        { return notes; }
    public void setNotes(String notes)              { this.notes = notes; }
    public Desk getDesk()                           { return desk; }
    public void setDesk(Desk desk)                  { this.desk = desk; }
    public User getUser()                           { return user; }
    public void setUser(User user)                  { this.user = user; }

    // ─── Builder ───────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDateTime startDateTime, endDateTime, createdAt = LocalDateTime.now();
        private ReservationStatus status = ReservationStatus.CONFIRMED;
        private BigDecimal totalPrice;
        private String notes;
        private Desk desk;
        private User user;

        public Builder id(Long v)                       { this.id = v; return this; }
        public Builder startDateTime(LocalDateTime v)   { this.startDateTime = v; return this; }
        public Builder endDateTime(LocalDateTime v)     { this.endDateTime = v; return this; }
        public Builder status(ReservationStatus v)      { this.status = v; return this; }
        public Builder totalPrice(BigDecimal v)         { this.totalPrice = v; return this; }
        public Builder notes(String v)                  { this.notes = v; return this; }
        public Builder createdAt(LocalDateTime v)       { this.createdAt = v; return this; }
        public Builder desk(Desk v)                     { this.desk = v; return this; }
        public Builder user(User v)                     { this.user = v; return this; }

        public Reservation build() {
            Reservation r = new Reservation();
            r.id = id; r.startDateTime = startDateTime; r.endDateTime = endDateTime;
            r.status = status; r.totalPrice = totalPrice; r.notes = notes;
            r.createdAt = createdAt; r.desk = desk; r.user = user;
            return r;
        }
    }
}
