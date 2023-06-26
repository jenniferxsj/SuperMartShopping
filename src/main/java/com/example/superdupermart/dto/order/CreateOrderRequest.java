package com.example.superdupermart.dto.order;
import com.example.superdupermart.dto.product.ProductRequest;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private List<ProductRequest> order;
}
