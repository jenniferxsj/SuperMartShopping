package com.example.superdupermart.domain;

import lombok.*;

import javax.persistence.*;

@Entity //want to map it to the database table
@Table(name="order_item")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "purchased_price")
    private double purchased_price;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "wholesale_price")
    private double wholesale_price;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
