package com.example.superdupermart.dto.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateProductRequest {
    private String description;
    private int quantity;
    private int retailPrice;
    private int wholesalePrice;
    private String name;
}
