package com.umc.danggeun.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserRes {
    private int userIdx;
    private String jwt;
}
