package com.umc.danggeun.wishlist;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.utils.JwtService;
import com.umc.danggeun.wishlist.model.WishListCount;
import com.umc.danggeun.wishlist.model.WishListSelectRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.umc.danggeun.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class WishListProvider {
    private final WishListDao wishListDao;
    private final JwtService jwtService;

    //유저 관심 목록 조회
    public List<WishListSelectRes> wishListSelect() throws BaseException {
        //토큰 유효기간 파악
        Date current = new Date(System.currentTimeMillis());
        if(current.after(jwtService.getExp())){
            throw new BaseException(INVALID_JWT);
        }
        int userIdx = jwtService.getUserIdx();
        int checkUserIdx = wishListDao.checkUserIdx(userIdx);
        if(checkUserIdx == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        //게시물이 없는지 조회
        try{
            List<WishListSelectRes> wishListSelect = wishListDao.wishListSelect(userIdx);
            return wishListSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 게시물 관심 목록 개수 조회
    public WishListCount wishListCount(int productIdx) throws BaseException{
        // 조회 가능한 게시물인지 확인
        int checkProductIdx = wishListDao.checkProductIdx(productIdx);
        if(checkProductIdx == 0){
            throw new BaseException(POST_POST_INVALID_PRODUCT);
        }

        try{
            WishListCount wishListCount = wishListDao.wishListCount(productIdx);
            return wishListCount;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
