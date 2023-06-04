package com.umc.danggeun.address.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRegionRes {
    private String city;
    private String district;
    private String regionName;
}
