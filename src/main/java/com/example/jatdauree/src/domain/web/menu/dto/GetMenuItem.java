package com.example.jatdauree.src.domain.web.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMenuItem implements Serializable {
    private int menuIdx;
    private String menuName;
    private int price;
    private String composition;
    private String description;
    private String menuUrl;
    private int isUpdated;
}
