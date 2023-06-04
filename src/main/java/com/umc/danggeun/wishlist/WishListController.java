package com.umc.danggeun.wishlist;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.config.BaseResponse;
import com.umc.danggeun.wishlist.model.PostWishListReq;
import com.umc.danggeun.wishlist.model.PostWishListRes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishListController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final WishListService wishListService;
//    @Autowired
//    private final JwtService jwtService;

    // 찜 상품 등록
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostWishListRes> postWishListRes(@RequestBody PostWishListReq postWishListReq){
        try{
            PostWishListRes postWishListRes = wishListService.createWishList(postWishListReq);
            return new BaseResponse(postWishListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
