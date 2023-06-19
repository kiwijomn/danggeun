package com.umc.danggeun.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchProductStatus {
    private int userIdx;
    private int productIdx;
    private int imgIdx;
    private String status;
}
