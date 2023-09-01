package com.example.jatdauree.src.domain.web.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMenuReq{
    @Nullable
    private List<PostMenuItem> mainMenuItems;
    @Nullable
    private List<PostMenuItem> sideMenuItems;
    @Nullable
    private List<PostIngredientItem> ingredientItems;
}