package eu.minted.komuna.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prices")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Viena kaina priklauso konkrečiam mokesčiui
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id", nullable = false)
    @JsonBackReference(value = "fee-prices")
    private Fee fee;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column
    private LocalDate validTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    @JsonBackReference(value = "price-community")
    private Community community;


    // === GET/SET ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Fee getFee() { return fee; }
    public void setFee(Fee fee) { this.fee = fee; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }

    public Community getCommunity() {return community;}

    public void setCommunity(Community community) {this.community = community;}
}
