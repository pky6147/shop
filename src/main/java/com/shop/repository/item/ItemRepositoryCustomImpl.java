package com.shop.repository.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import lombok.RequiredArgsConstructor;

//import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

//import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.shop.entity.QItem.item;
import static com.shop.entity.QItemImg.itemImg;


@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private BooleanExpression regDtsAfter(String searchDateType){
        /*
        searchDateType 화면 ==> "all", "id", "1w", "1m", "6m"
        * */
        LocalDateTime now = LocalDateTime.now();
        if(StringUtils.equals(searchDateType,"1d")) {
            now = now.minusDays(1);
        }
        else if (StringUtils.equals(searchDateType,"1w")) {
            now = now.minusWeeks(1);
        }
        else if (StringUtils.equals(searchDateType,"1M")) {
            now = now.minusMonths(1);
        }
        else if (StringUtils.equals(searchDateType,"6M")) {
            now = now.minusMonths(6);
        }
        else if (StringUtils.equals(searchDateType,"all") || searchDateType == null) {
            return null;
        }

        return item.regTime.after(now);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus itemSellStatus){
        if(itemSellStatus == null){
            return null;
        }
        return item.itemSellStatus.eq(itemSellStatus);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        //무엇을 기준으로(searchBy) 검색할 키워드(searchQuery)
        //searchBy 화면 ==> itemNm, createBy
        if(searchBy.equals("itemNm") ) {
            return item.itemNm.like("%" + searchQuery + "%");
        }
        else if (searchBy.equals("createBy")) {
            return item.createBy.like("%" + searchQuery + "%");
        }
        return null;
    }


    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        /*
        목적: item 테이블에서 검색조건에 맞는 결과를 페이지 단위로 조회
        조건1. searchDateType에 따라 검색기간 설정
        조건2. searchSellStatus에 따라 상품 판매 상태(SELL, SOLE_OUT) 설정
        조건3. searchBy + searchQuery에 따라 검색 키워드 설정
        ==>  item_id를 기준으로 내림차순, pageable 기준에 따른 페이징 결과 반환

        select *
        from item
        where 조건1 and 조건2 and 조건3
        order by item_id desc
        limit offset

        Page(인터페이스) PageImpl(구현체)
        PageImpl
        -content: List<T>
        -totalCount: 총 페이지 수
        -number: 페이지 번호
        * */

        List<Item> content = jpaQueryFactory
                .selectFrom(item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory.select(Wildcard.count).from(item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
        .fetchOne();

        Optional<Long> total = Optional.ofNullable(totalCount);
        return new PageImpl<>(content, pageable, total.orElse(0L));

        //조건 1~3 중 null이 들어가는 경우 무시생략 그 쿼리 없는 셈쳐라


    }

    private BooleanExpression itemNumLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<MainItemDto> content = jpaQueryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price
                        )
                ).from(itemImg)
                .innerJoin(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(searchByLike("itemNm", itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .innerJoin(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(searchByLike("itemNm", itemSearchDto.getSearchQuery()))
                .fetchOne();
        Optional<Long> total = Optional.ofNullable(totalCount);
        return new PageImpl<>(content, pageable, total.orElse(0L));
    }
}
