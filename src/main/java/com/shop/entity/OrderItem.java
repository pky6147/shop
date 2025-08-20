package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="order_item")
@Getter
@Setter
public class OrderItem extends BaseEntity {
    @Id
    @Column(name="order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY) // 기본값 즉시 로딩 (join한 결과)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private Integer orderPrice;

    private Integer count;


    public static OrderItem createOrderItem(Item item, Integer count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());

        item.removeStock(count);
        return orderItem;
    }

    public Integer getTotalPrice() {
        return orderPrice*count;
    }
//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;

    public void cancel() {
        this.item.addStock(count);
    }
}
