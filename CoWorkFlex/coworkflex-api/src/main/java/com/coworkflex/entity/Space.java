package com.coworkflex.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "spaces")
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String address;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer capacity;

    private String description;
    private String imageUrl;

    @DecimalMin("0.0") @DecimalMax("5.0")
    private Double rating = 0.0;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Desk> desks = new ArrayList<>();

    public Space() {}

    // ─── Getters / Setters ─────────────────────────────────────
    public Long getId()                     { return id; }
    public void setId(Long id)              { this.id = id; }
    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }
    public String getCity()                 { return city; }
    public void setCity(String city)        { this.city = city; }
    public String getAddress()              { return address; }
    public void setAddress(String address)  { this.address = address; }
    public Integer getCapacity()            { return capacity; }
    public void setCapacity(Integer capacity){ this.capacity = capacity; }
    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }
    public String getImageUrl()             { return imageUrl; }
    public void setImageUrl(String url)     { this.imageUrl = url; }
    public Double getRating()               { return rating; }
    public void setRating(Double rating)    { this.rating = rating; }
    public List<Desk> getDesks()            { return desks; }
    public void setDesks(List<Desk> desks)  { this.desks = desks; }

    public void addDesk(Desk desk) {
        desks.add(desk);
        desk.setSpace(this);
    }

    // ─── Builder ───────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name, city, address, description, imageUrl;
        private Integer capacity;
        private Double rating = 0.0;

        public Builder id(Long v)           { this.id = v; return this; }
        public Builder name(String v)       { this.name = v; return this; }
        public Builder city(String v)       { this.city = v; return this; }
        public Builder address(String v)    { this.address = v; return this; }
        public Builder capacity(Integer v)  { this.capacity = v; return this; }
        public Builder description(String v){ this.description = v; return this; }
        public Builder imageUrl(String v)   { this.imageUrl = v; return this; }
        public Builder rating(Double v)     { this.rating = v; return this; }

        public Space build() {
            Space s = new Space();
            s.id = id; s.name = name; s.city = city; s.address = address;
            s.capacity = capacity; s.description = description;
            s.imageUrl = imageUrl; s.rating = rating;
            return s;
        }
    }
}
