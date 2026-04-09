package com.ljuslin.wigellMCRental.dto;
public record ErrorResponse(
        String timeStamp,
        int status,
        String error,
        String message,
        String path
) {}
