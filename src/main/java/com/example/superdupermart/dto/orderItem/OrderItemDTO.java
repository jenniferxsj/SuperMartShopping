package com.example.superdupermart.dto.orderItem;

import com.example.superdupermart.dto.product.ProductDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemDTO {
    int id;
    double purchased_price;
    double wholesale_price;
    int quantity;
    ProductDTO productDTO;
}
