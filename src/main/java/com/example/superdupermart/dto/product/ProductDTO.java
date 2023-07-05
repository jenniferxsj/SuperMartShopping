package com.example.superdupermart.dto.product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ProductDTO {
    int id;
    String name;
    String description;
}
