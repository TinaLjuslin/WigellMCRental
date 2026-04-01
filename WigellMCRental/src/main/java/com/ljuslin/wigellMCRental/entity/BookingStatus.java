package com.ljuslin.wigellMCRental.entity;

public enum BookingStatus {
    BOOKED("bokad"),
    RETURNED("returnerad"),
    CANCELLED("avbokad");

    private final String swedishName;

    BookingStatus(String swedishName) {
        this.swedishName = swedishName;
    }

    public String getSwedish() {
        return swedishName;
    }
}
