package com.shop.repository.cartItem;

import com.shop.dto.CartDetailDto;
import com.shop.entity.Cart;

import java.util.List;

public interface CartItemRepositoryCustom {



    /*
    select A.id, B.itemNn, B.price, A.count, C.imgUrl
    from cart_Item A
    inner join Item B on A.itemId = B.item_id
    inner join item_img C on B.item_id = C.item_id
    where A.cartId = ? and C.repImgYn = "Y"
    order by A.regTime desc
    */
    List<CartDetailDto> findCartDetailDtoList(Long cartId);
}
