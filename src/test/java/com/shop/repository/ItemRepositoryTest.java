package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import static com.shop.entity.QItem.item; //스테틱 필드를 바로 임포트


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
public class ItemRepositoryTest {


    @PersistenceContext //영속성 컨텍스트 EntityManager 주입
    EntityManager em;


    @Autowired
    public ItemRepository itemRepository;
    @Test
    @DisplayName("상품저장테스트")
    public void createItemTest() {
        //DB에 값 저장(insert)하기
        //1. Entity 객체를 만든다.
        //2. Entity 객체에 저장하고 싶은 값을 넣는다.
        //3. JPA Repository를 이용해 저장(save => persist + flush)한다.
        Item item = new Item();
        item.setItemNm("테스트실행");
        item.setPrice(100);
        item.setItemDetail("테스트상품설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem);
    }

    @Test
    @DisplayName("상품조회테스트")
    public void findByItemNmTest() {
        createDummyItems();
        List<Item> items = itemRepository.findByItemNm("테스트상품1");
        for ( Item item : items) {
            System.out.println(item);
        }

    }


    public void createDummyItems() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트상품" +i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트상품설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100 + i);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
    }

 }

    public void createDummyItems2() {
        for( int i  = 1; i <= 5; i++ ) {
            Item item = new Item();
            item.setItemNm("테스트상품" +i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트상품설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100 + i);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
        for (int i = 6; i <= 10; i++ ) {
            Item item = new Item();
            item.setItemNm("테스트상품" +i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트상품설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }



 @Test
 @DisplayName("상품이름과 상품설명 or 테스트")
 public void findByItemNmOrItemDetailTest() {
        createDummyItems();
        createDummyItems();
        List<Item> items = itemRepository.findByItemNmOrItemDetail("테스트상품1", "테스트상품설명5");
        for ( Item item : items) {
            System.out.println(item);
        }
 }

 @Test
 @DisplayName("가격 Lessthan test")
    public void findByItemNmOrItemDetailLessThanTest() {
     createDummyItems();
     List<Item> items = itemRepository.findByPriceLessThan(10005);
     for ( Item item : items) {
         System.out.println(item);
     }
 }

 @Test
 @DisplayName("가격 내림차순")
 public void findByPriceLessThanOrderByPriceDescTest() {
     createDummyItems();
     List<Item> items = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
     for ( Item item : items) {
         System.out.println(item);
     }

 }


    @Test
    @DisplayName("상품설명 가격 내림차순")
    public void findByItemDetailTest() {
        createDummyItems();
        List<Item> items = itemRepository.findByItemDetail("테스트상품설명");
        for ( Item item : items) {
            System.out.println(item);
        }
    }


    @Test
    @DisplayName("상품설명 가격 내림차순 By Native")
    public void findByItemDetailByNativeTest() {
        createDummyItems();
        List<Item> items = itemRepository.findByItemDetailByNative("테스트상품설명");
        for ( Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("QueryDSL 조회 테스트")
    public void queryDslTest() {
        createDummyItems();
        //select * from item where item_sell_status = sell and item_detail like %테스트 상품 상세 설명% order by price des
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<Item> query = queryFactory.selectFrom(item) //select * from item
                .where(item.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(item.itemDetail.like("%"+"테스트상품설명"+"%"))
                .orderBy(item.price.desc());

        List<Item> items = query.fetch();
        for ( Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("QueryDSL 조회 테스트2")
    public void queryDslTest2() {
        createDummyItems2();
        String itemDetail = "테스트상품설명";
        int price = 10_003;
        String itemSellStatus = "SELL";
        int pageNum = 1;

        //조건1. 주어진 itemDetail 키워드 포함
        //조건2. 가격이 주어진 price 보다 큰
        //조건3. 조회하려는 상태가 SELL인 경우 상품의 판매 상태가 SELL 인 경우
        //조건4. 한 페이지 당 5개씩 페이징된 데이터를 조회
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<Item> baseQuery = queryFactory.selectFrom(item);

        BooleanBuilder booleanbuilder = new BooleanBuilder();
        booleanbuilder.and(item.itemDetail.like("%"+itemDetail+"%"));
        booleanbuilder.and (item.price.gt(price));
        if(itemSellStatus.equals("SELL")) {
            booleanbuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }
        JPAQuery<Item> conditionedQuery = baseQuery.where(booleanbuilder);
        //select * from item where itemDetail like ? and price < ? and item_sell_status = "SELL"

        Pageable pageable = PageRequest.of(pageNum -1, 5);
        JPAQuery<Item> pagedQuery = conditionedQuery
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        //select * from item where itemDetail like ? and price < ? and item_sell_status = "SELL" order by id desc limit 5 Offset ?

        List<Item> contents = pagedQuery.fetch();
        Long totalCount = queryFactory.select(Wildcard.count).from(item).where(booleanbuilder).fetchOne();
        //select count(*) from item
        Page<Item> result = new PageImpl<>(contents, pageable, totalCount);

        System.out.println("총 컨텐츠 요소의 수 :"+result.getTotalElements());
        System.out.println("조회가능한 총 페이지 수 :"+result.getTotalPages());


        for(Item item : contents) {
            System.out.println(item);
        }
    }

}
