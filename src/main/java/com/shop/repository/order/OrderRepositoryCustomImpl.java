package com.shop.repository.order;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;



import java.util.List;
import java.util.Optional;

import static com.shop.entity.QOrder.order;


@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Order> findOrders(String email, Pageable pageable) {
        /*
        select *
        from orders A
        inner join  member B on A.member_id = B.member_id
        where B.email = ?
        order by A.order_date desc
        * */
        List<Order> orders = jpaQueryFactory
                .select(order)
                .from(order)
                .innerJoin(order.member)
                .where(order.member.email.eq(email))
                .orderBy(order.orderDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return orders;
    }

    @Override
    public Long countOrders(String email) {
        /*
        select count(*)
        from orders A
        inner join  member B on A.member_id = B.member_id
        where B.email = ?

        * */
        Long total = jpaQueryFactory
                .select(Wildcard.count)
                .from(order)
                .innerJoin(order.member)
                .where(order.member.email.eq(email))
                .fetchOne();

        Optional<Long> totalCount = Optional.ofNullable(total);
        return totalCount.orElse(0L);
    }
}
