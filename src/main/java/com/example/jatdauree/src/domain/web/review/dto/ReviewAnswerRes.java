package com.example.jatdauree.src.domain.web.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewAnswerRes {
    private int storeIdx;
    private int reviewIdx;
    private String comment;
}
