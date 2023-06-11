package com.umc.danggeun.wishlist;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.wishlist.model.PostWishListReq;
import com.umc.danggeun.wishlist.model.PostWishListRes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.umc.danggeun.config.BaseResponseStatus.*;


// Service Create, Update, Delete 의 로직 처리
@Service
@RequiredArgsConstructor
public class WishListService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WishListDao wishListDao;

    // 찜 상품 추가
    public PostWishListRes createWishList(PostWishListReq postWishListReq) throws BaseException {
        // 정상 유저인지 확인
        int checkUserIdx = wishListDao.checkUserIdx(postWishListReq.getUserIdx());
        if(checkUserIdx == 0){
            throw new BaseException(POST_POST_INVALID_USER);
        }
        // 찜 등록 가능한 상품인지 확인
        int checkProductIdx = wishListDao.checkProductIdx(postWishListReq.getProductIdx());
        if(checkProductIdx == 0){
            throw new BaseException(POST_POST_INVALID_PRODUCT);
        }

        try{
            int wishIdx = wishListDao.createWishList(postWishListReq);
            return new PostWishListRes(wishIdx);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
