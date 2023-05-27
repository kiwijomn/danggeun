package com.umc.danggeun.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private int userIdx;
    private String phone;
    private String nickname;
    private String email;
    private String profileImg;
}
