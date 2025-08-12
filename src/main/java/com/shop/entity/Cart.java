package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity {
    @Id
    @Column(name = "cart_id") //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    //member table 의 member_id(pk)를 참조하는 cart table의 member_id(fk)
    //@JoinColumn(name = fk컬럼이름 ) == 부모 테이블 이름과 동일하게 하기
    //부모 참조를 알리는 과정
    @OneToOne(fetch = FetchType.LAZY) //one == cart to one == member ==> 1:1
    @JoinColumn(name = "member_id", unique = true ) //fk
    private Member member; //참조될 만한 것 들고온거임
    //private Long member_id X
}
