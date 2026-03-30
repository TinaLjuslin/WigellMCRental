package com.ljuslin.wigellMCRental.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "bike")
public class Bike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String engineSize;
    @Column(nullable = false)
    private String model;
    @Column(name = "production_year", nullable = false )
    private int year;
    @Column(name = "price_per_day", precision = 10, scale = 2)
    private BigDecimal pricePerDay;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BikeStatus status = BikeStatus.AVAILABLE;

    protected Bike() {
    }

    public Bike(Long id, String brand, String engineSize, String model,
                int year, BigDecimal pricePerDay) {
        this.id = id;
        this.brand = brand;
        this.engineSize = engineSize;
        this.model = model;
        this.year = year;
        this.pricePerDay = pricePerDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(String engineSize) {
        this.engineSize = engineSize;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public BikeStatus getStatus() {
        return status;
    }

    public void setStatus(BikeStatus status) {
        this.status = status;
    }
}
