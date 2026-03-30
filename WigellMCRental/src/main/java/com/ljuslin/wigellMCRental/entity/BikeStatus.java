package com.ljuslin.wigellMCRental.entity;

public enum BikeStatus {
    AVAILABLE("Tillgänglig"),
    IN_SERVICE("På service"),
    RETIRED("Tagits ur bruk");

    private final String swedishName;

    BikeStatus(String swedishName) {
        this.swedishName = swedishName;
    }

    public String getSwedish() {
        return swedishName;
    }
}