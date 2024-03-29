package com.example.jatdauree.src.domain.web.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesByWeekDay {
    private String date;
    private int day;
    private int totalPrice;
}
