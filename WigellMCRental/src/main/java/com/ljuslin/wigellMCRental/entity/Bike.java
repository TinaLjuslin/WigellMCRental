package com.ljuslin.wigellMCRental.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bike")
public class Bike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String model;
    private String enginSize;
    private int year;
}
