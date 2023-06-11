package com.umc.danggeun.wishlist.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WishListSelectRes {
    private int wishIdx;
    private int productIdx;
    private int regionIdx;
    private String title;
    private int categoryIdx;
    private int price;
    private String status;
}
