package com.example.jatdauree.src.domain.app.basket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasketExist {
    private int basketIdx;
    private int storeIdx;
    private int todaymenuIdx;
    private int remain;
    private String status;

}
