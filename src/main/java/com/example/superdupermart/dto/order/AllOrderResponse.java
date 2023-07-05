package com.example.superdupermart.dto.order;

import com.example.superdupermart.domain.Order;
import com.example.superdupermart.dto.common.ServiceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AllOrderResponse {
    ServiceStatus serviceStatus;
    List<OrderDTO> orders;
}
