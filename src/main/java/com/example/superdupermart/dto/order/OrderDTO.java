package com.example.superdupermart.dto.order;

import com.example.superdupermart.dto.orderItem.OrderItemDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDTO {
    int id;
    Timestamp date_placed;
    String order_status;
    String username;
    List<OrderItemDTO> orderItemDTOList;
}
