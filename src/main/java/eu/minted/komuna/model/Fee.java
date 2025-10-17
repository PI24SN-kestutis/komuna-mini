package eu.minted.komuna.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fees")
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // pvz. "Vanduo", "Šildymas"

    @Column
    private String unit; // pvz. "m³", "kWh"

    @Column
    private String description;

    // Bendrija, kuriai priskirta ši paslauga (mokestis)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = true)
    @JsonBackReference(value = "community-fees") // ← SUTAPTI su Community.java
    private Community community;

    // Kainos
    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "fee-prices")
    private List<Price> prices = new ArrayList<>();

    @Column(nullable = true)
    private boolean paid = false;

    // === GET/SET ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Community getCommunity() { return community; }
    public void setCommunity(Community community) { this.community = community; }

    public List<Price> getPrices() { return prices; }
    public void setPrices(List<Price> prices) { this.prices = prices; }

    public boolean isPaid() {return paid;}
    public void setPaid(boolean paid) {this.paid = paid;}
}
