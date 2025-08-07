package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//상속해서 자동 빈인식
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByItemNm(String itemNm);
    //select * from item where item_nm = ?
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    //select * from item where item_nm = ? or item_Detail = ?
    List<Item> findByPriceLessThan(Integer price);
    //select * from item where price < ?
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    //select * from item where price < ? order by desc

//    JPQL은 from Item은 클래스 Entity명 item X
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail")String itemDetail);


    @Query(value = "select * from item i where i.item_Detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(String itemDetail);
}
