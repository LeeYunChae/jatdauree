package com.example.jatdauree.src.domain.web.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchMenuItem implements Serializable {
    private int menuIdx;
    private String menuName;
    private int price;
    private String composition;
    private String description;
    @Nullable
    private MultipartFile menuUrl;
    private int isUpdated;
    // 0: 수정안하고 그대로 유지메뉴
    // 1: 수정할 메뉴
    // 2: 삭제할 메뉴
    // 3: 새로운 메뉴
}
