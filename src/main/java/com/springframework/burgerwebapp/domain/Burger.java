package com.springframework.burgerwebapp.domain;

import org.hibernate.annotations.Cascade;
import javax.persistence.*;

@Entity
public class Burger {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String burger;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    //Burger Table 'Venue_id' = Venue ID.
    private Venue venue;

    private String img;

    public Burger() {
    }

    public Burger(String burger, String img) {
        this.burger = burger;
        this.img = img;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBurger() {
        return burger;
    }

    public void setBurger(String burger) {
        this.burger = burger;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "Burger{" +
                "id=" + id +
                ", burger='" + burger + '\'' +
                ", img='" + img + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Burger burger = (Burger) o;

        return id != null ? id.equals(burger.id) : burger.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
