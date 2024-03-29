package com.example.jatdauree.src.domain.web.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBillRes implements Serializable {
    private int storeIdx;
    private int orderIdx;
    private String orderDate;
    private String paymentStatus;
    private String pickUpTime;
    private int orderSequence;
    private String request;
    private List<OrderMenuCnt> orderItem;

}
