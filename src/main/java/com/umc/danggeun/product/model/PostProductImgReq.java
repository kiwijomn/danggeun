package com.umc.danggeun.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostProductImgReq {
    private int productIdx;
    private String imgUrl;
}
