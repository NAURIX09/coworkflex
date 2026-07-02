package com.coworkflex.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "desks")
public class Desk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeskType type;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    private String amenities;

    @Column(nullable = false)
    private Boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @OneToMany(mappedBy = "desk", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public Desk() {}

    // ─── Getters / Setters ─────────────────────────────────────
    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public String getLabel()                    { return label; }
    public void setLabel(String label)          { this.label = label; }
    public DeskType getType()                   { return type; }
    public void setType(DeskType type)          { this.type = type; }
    public BigDecimal getPricePerHour()         { return pricePerHour; }
    public void setPricePerHour(BigDecimal p)   { this.pricePerHour = p; }
    public String getAmenities()                { return amenities; }
    public void setAmenities(String amenities)  { this.amenities = amenities; }
    public Boolean getAvailable()               { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public Space getSpace()                     { return space; }
    public void setSpace(Space space)           { this.space = space; }
    public List<Reservation> getReservations()  { return reservations; }

    // ─── Builder ───────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String label, amenities;
        private DeskType type;
        private BigDecimal pricePerHour;
        private Boolean available = true;
        private Space space;

        public Builder id(Long v)               { this.id = v; return this; }
        public Builder label(String v)          { this.label = v; return this; }
        public Builder type(DeskType v)         { this.type = v; return this; }
        public Builder pricePerHour(BigDecimal v){ this.pricePerHour = v; return this; }
        public Builder amenities(String v)      { this.amenities = v; return this; }
        public Builder available(Boolean v)     { this.available = v; return this; }
        public Builder space(Space v)           { this.space = v; return this; }

        public Desk build() {
            Desk d = new Desk();
            d.id = id; d.label = label; d.type = type;
            d.pricePerHour = pricePerHour; d.amenities = amenities;
            d.available = available; d.space = space;
            return d;
        }
    }
}
