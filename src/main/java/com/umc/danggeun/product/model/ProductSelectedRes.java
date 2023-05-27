package com.umc.danggeun.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductSelectedRes {
    private int productIdx;
    private int sellerIdx;
    private String regionName;
    private int categoryIdx;
    private String title;
    private String content;
    private int price;
    private String time;
    private String status;
}
