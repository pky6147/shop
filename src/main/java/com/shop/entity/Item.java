package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString

public class Item extends BaseEntity {
    @Id
    @Column(name ="item_id") //DB컬럼명
    @GeneratedValue(strategy = GenerationType.IDENTITY)//자동증가
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemNm;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockNumber;

    @Lob//DB의 타입 Long으로 인식
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        
    }

    public void removeStock(Integer stockNumber) {
        Integer restStock = this.stockNumber - stockNumber;
        if(restStock<0) {
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(Integer stockNumber) {
        this.stockNumber += stockNumber;
    }

//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;
}
