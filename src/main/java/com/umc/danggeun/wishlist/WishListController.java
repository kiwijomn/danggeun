package com.umc.danggeun.wishlist;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.config.BaseResponse;
import com.umc.danggeun.wishlist.model.PostWishListReq;
import com.umc.danggeun.wishlist.model.PostWishListRes;
import com.umc.danggeun.wishlist.model.WishListCount;
import com.umc.danggeun.wishlist.model.WishListSelectRes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishListController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final WishListService wishListService;
    @Autowired
    private final WishListProvider wishListProvider;
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

    // 찜 목록 조회
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<WishListSelectRes>> wishListSelect(){
        try{
            List<WishListSelectRes> wishListSelectRes = wishListProvider.wishListSelect();
            return new BaseResponse<>(wishListSelectRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //게시물의 관심 목록 개수
    @ResponseBody
    @GetMapping("/count/{productIdx}")
    public BaseResponse<WishListCount> wishListCount(@PathVariable("productIdx") int productIdx){
        try{
            WishListCount wishListCount = wishListProvider.wishListCount(productIdx);
            return new BaseResponse(wishListCount);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
