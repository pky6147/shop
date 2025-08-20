package com.shop.repository.cartItem;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.dto.CartDetailDto;
import lombok.RequiredArgsConstructor;

import static com.shop.entity.QCartItem.cartItem;
import static com.shop.entity.QItem.item;
import static com.shop.entity.QItemImg.itemImg;
import java.util.List;

@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
      /*
    select A.id, B.itemNn, B.price, A.count, C.imgUrl
    from cart_Item A
    inner join Item B on A.itemId = B.item_id
    inner join item_img C on B.item_id = C.item_id
    where A.cartId = ? and C.repImgYn = "Y"
    order by A.regTime desc
    */

    @Override
    public List<CartDetailDto> findCartDetailDtoList(Long cartId) {

        return jpaQueryFactory
                .select(Projections.fields(CartDetailDto.class,
                        cartItem.id.as("cartItemId"),
                        item.itemNm,
                        item.price,
                        cartItem.count,
                        itemImg.imgUrl))
                .from(item)
                .join(cartItem)
                .on(cartItem.item.eq(item))
                .join(itemImg)
                .on(itemImg.item.eq(item))
                .where(cartItem.cart.id.eq(cartId))
                .where(itemImg.repImgYn.eq("Y"))
                .orderBy(cartItem.regTime.desc())
                .fetch();


    }
}
