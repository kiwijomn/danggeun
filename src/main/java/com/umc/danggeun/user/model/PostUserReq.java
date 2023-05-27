package com.umc.danggeun.user.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String nickname;
    private String phone;
    private String email;
    private String regionName; // Region Table
}
