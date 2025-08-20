package com.shop.service;

import com.shop.dto.*;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.cartItem.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email) {
        /*
        Item Entity 조회
        Member Entity 조회
        Cart Entity 조회 > 없다 > Cart 만들기
        CartItem 조회 > 없다 > CartItem Entity 저장 || 있다 > 조회된 CartItem count 증가
        * */

        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMember(member);
        if(cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartAndItem(cart, item);
        if(savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        }
        else {
            CartItem newCartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(newCartItem);
            return newCartItem.getId();
        }

    }


    public List<CartDetailDto> getCartList(String email) {
        /*
        member 조회 > cart 조회 > cartItem 조회
        * */
        Member member =  memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMember(member);
        if(cart == null) {
            return new ArrayList<>();
        }




        //특정 카트에 들어있는 카트 아이템 리스를 조회하기

        return cartItemRepository.findCartDetailDtoList(cart.getId());

    }

    public void updateCartItemCount(Long cartItemId, Integer count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        //1
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
        //2
//      cartItemRepository.deleteById(cartItemId);
    }


    public Long orderCartItems(List<CartOrderDto> cartOrderDtoList, String email) {
        //책임1. CartItemDto에 있는 cartItemId를 이용 ==> OrderDtoList를 생성
        List<OrderDto> orderDtoList = new ArrayList<>();
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            OrderDto orderDto = OrderDto.builder()
                    .itemId(cartItem.getItem().getId())
                    .count(cartItem.getCount())
                    .build();
            orderDtoList.add(orderDto);
        }
        //주문 ==> 수행 ==> orderService ==> OrderDtoList, email
        Long orderId = orderService.orders(orderDtoList, email);
        //책임2. 주문이 완료된 cartItem을 테이블에서 삭제한다
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            cartItemRepository.deleteById(cartOrderDto.getCartItemId());
        }
        return orderId;
    }
}
