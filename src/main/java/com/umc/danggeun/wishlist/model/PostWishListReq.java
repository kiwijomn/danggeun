package com.umc.danggeun.wishlist.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostWishListReq {
    private int userIdx;
    private int productIdx;
}
