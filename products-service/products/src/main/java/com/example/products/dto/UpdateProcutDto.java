package com.example.products.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProcutDto {
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Size(min = 6, max = 255, message = "Description must be between 6 and 255 characters")
    private String description;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    private String image;
}