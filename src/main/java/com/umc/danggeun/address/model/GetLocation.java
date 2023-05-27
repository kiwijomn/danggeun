package com.umc.danggeun.address.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class GetLocation {
    private BigDecimal latitude;
    private BigDecimal longitude;
}
