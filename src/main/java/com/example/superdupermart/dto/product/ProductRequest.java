package com.example.superdupermart.dto.product;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private int productId;
    private int quantity;
}
