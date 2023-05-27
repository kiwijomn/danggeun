package com.umc.danggeun.address.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetNearRegionListRes {
    private List<Integer> range1;
    private List<Integer> range2;
    private List<Integer> range3;
    private List<Integer> range4;
}
