package com.shop.entity;

import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
public class Order extends BaseEntity {
    @Id
    @Column(name="order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    //OneToMany 일대다 일:Order 다:OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY) //(mappedBy = "order -> Order 필드명") // read-only => DB 표현 불가 JPA만 있음
    List<OrderItem> orderItems = new ArrayList<>(); //읽기전용으로 주인은 order

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;

}
