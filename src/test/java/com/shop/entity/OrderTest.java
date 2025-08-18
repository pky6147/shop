package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.repository.item.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
class OrderTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @PersistenceContext
    EntityManager em;

    public Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10_000);
        item.setItemDetail("상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    public Order createOrder() {
        Order order = new Order();
        for (int i = 0; i < 3; i++ ) {
            Item item = createItem();
            itemRepository.save(item);
            //OrderItem (주문과 특정 아이템을 연결해주는 역할)
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setOrder(order);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            /* 현재 Order는 저장이 완됨, order 엔티티의 orderItems 리스트에 orderItem 엔티티를 추가*/
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);
        orderRepository.save(order);
        return order;
    }



    @Test
    @DisplayName("영속성 전이 테스트")
    void cascadeTest() {
        Order order = new Order();
        //주문 Entity 생성
        //아이템기준정보생성, 그 기준 정보로 주문 아이템을 생성
        for (int i = 0; i < 3; i++ ) {
            //아이템 Entity 저장
            Item item = createItem();
            itemRepository.save(item);
            //OrderItem (주문과 특정 아이템을 연결해주는 역할)
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setOrder(order);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            /* 현재 Order는 저장이 완됨, order 엔티티의 orderItems 리스트에 orderItem 엔티티를 추가*/
            order.getOrderItems().add(orderItem);
        }
        /*지금 Order는 저장 전에 OrderItem 엔티티를 3개 포함한 상태*/
        orderRepository.saveAndFlush(order);
        em.clear();

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(3, savedOrder.getOrderItems().size());
    }


    @Test
    @DisplayName("고아객체 제거 테스트")
    void orphanRemovalTest() {
        Order order = createOrder();
        order.getOrderItems().remove(0); // 영속성 컨텍스트에 사라지고(객체에서 고아됨) 인식해서 Delete 호출
        em.flush();

    }


    @Test
    @DisplayName("지연로딩 테스트")
    void lazyLoadingTest() {
        Order order = createOrder();
        OrderItem orderItem = order.getOrderItems().get(0);
        Long orderItemId = orderItem.getId();
        em.flush();
        em.clear();
        OrderItem savedOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class: " + savedOrderItem.getOrder().getClass());
        System.out.println("==================");
        savedOrderItem.getOrder().getOrderDate();
        System.out.println("==================");
        savedOrderItem.getItem().getItemDetail();

    }


}