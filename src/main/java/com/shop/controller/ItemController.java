package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping("/admin/item/new")
    public String itemNew(@Valid @ModelAttribute ItemFormDto itemFormDto, BindingResult bindingResult, Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
        if(bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute( "errorMessage", "첫번째 상품 이미지는 필수 입력 입니다.");
            return "item/itemForm";
        }
        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e) {
            model.addAttribute("errorMessage", "상품등록 중 오류가 발생했습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }


    @GetMapping("/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        }catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            return "item/itemForm";
        }
        return "item/itemForm";
    }


    @PostMapping("/admin/item/{itemId}")
    public String itemUpdate(@Valid @ModelAttribute ItemFormDto itemFormDto, BindingResult bindingResult, Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
        if(bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute( "errorMessage", "첫번째 상품 이미지는 필수 입력 입니다.");
            return "item/itemForm";
        }
        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        }catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 오류가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

}
