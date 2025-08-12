package com.shop.dto;


import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {

    private Long id;
    private String imgName;
    private String orgImgName;
    private String imgUrl;
    private String repImgYn;


    private static ModelMapper modelMapper = new ModelMapper();


    private static ItemImgDto of(ItemImg itemImg) {
//        ModelMapper mapper  = new ModelMapper();
        return modelMapper.map(itemImg, ItemImgDto.class);// modelmapper 사용해서 Entity > Dto 변환된 객체 반환
    }
}
