package com.umc.danggeun.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostProductReq {
    private int sellerIdx;
    private int regionIdx;
    private int categoryIdx;
    private String title;
    private String content;
    private int price;
    private String proposal;
    private String share;
}
