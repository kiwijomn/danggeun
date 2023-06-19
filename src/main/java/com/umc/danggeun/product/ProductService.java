package com.umc.danggeun.product;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.product.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.umc.danggeun.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class ProductService {
//    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductDao productDao;
//    private final PostProvider postProvider;
//    private final JwtService jwtService;

    // 중고상품 게시글 작성
    @Transactional
    public PostProductRes postProduct(PostProductReq postProductReq) throws BaseException {
        try{
            int productIdx = productDao.createProduct(postProductReq);
            return new PostProductRes(productIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostProductImgRes productImage(PostProductImgReq postProductImgReq) throws BaseException{
        try{
            int[] imgIdx = productDao.createProductImage(postProductImgReq);
            return new PostProductImgRes(imgIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void patchProductComplete(int productIdx, int userIdByJwt, int buyerIdx) throws BaseException {
        //0. postId로 삭제된 post인지 확인
        //1. postId로 userId 조회
        //2. userId와 userIdByJwt가 같은 지 조회
        //3. postId가 complete 상태인지 조회: status = C
        //4. 없다면 추가, 있다면 Valid
        //5. post에서 postId에 해당하는 status invalid 처리

        //0.
        if(productDao.checkProductDeleted(productIdx) == 1){
            throw new BaseException(MODIFY_FAIL_INVALID_POST);
        }
        //1.
        try{
            int userIdx = productDao.getUserIdxByProductIdx(productIdx);
            //2.
            if(userIdx != userIdByJwt){
                throw new BaseException(INVALID_USER_JWT);
            }

            //3. dealcomplete에 이미 존재한다면
            if(productDao.checkTradeComplete(productIdx) == 1){
                //4
                productDao.setTradeUserComplete(productIdx, userIdx, buyerIdx);
            }else{
                // dealcomplete에 없다면
                // 4
                productDao.postTradeComplete(productIdx, userIdx, buyerIdx);
            }
            // 5
            productDao.patchProductStatusComplete(productIdx, "C");
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void patchProductSale(int productIdx, int userIdByJwt) throws BaseException {
        //0. postId로 삭제된 post인지 확인
        //1. postId로 userId 조회
        //2. userId와 userIdByJwt가 같은 지 조회
        //3. postId가 dealComplete에 있는 지 조회
        //4. 있다면 Invalid
        //5. post에서 postId에 해당하는 status Valid 처리

        //0.
        if(productDao.checkProductDeleted(productIdx) == 1){
            throw new BaseException(MODIFY_FAIL_INVALID_POST);
        }
        //1.
        try{
            int userIdx = productDao.getUserIdxByProductIdx(productIdx);
            //2.
            if(userIdx != userIdByJwt){
                throw new BaseException(INVALID_USER_JWT);
            }

            //3. dealcomplete에 이미 존재한다면
            if(productDao.checkTradeComplete(productIdx) == 1){
                //4
                productDao.patchTradeCompleteToSale(productIdx);
            }
            productDao.patchProductStatusComplete(productIdx, "Y");

        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void patchProductReserved(int productIdx, int userIdByJwt, int reservationIdx) throws BaseException {
        //0. postId로 삭제된 post인지 확인
        //1. postId로 userId 조회
        //2. userId와 userIdByJwt가 같은 지 조회
        //3. postId가 dealComplete에 있는 지 조회
        //4. 없다면 추가 있다면 Reserved
        //5. post에서 postId에 해당하는 status Valid 처리

        //0.
        if(productDao.checkProductDeleted(productIdx) == 1){
            throw new BaseException(MODIFY_FAIL_INVALID_POST);
        }
        //1.
        try{
            int userIdx = productDao.getUserIdxByProductIdx(productIdx);
            //2.
            if(userIdx != userIdByJwt){
                throw new BaseException(INVALID_USER_JWT);
            }

            //3. dealcomplete에 이미 존재한다면
            if(productDao.checkTradeComplete(productIdx) == 1){
                //4
                productDao.patchTradeCompleteToReserved(productIdx, userIdx, reservationIdx);
            }else{
                // dealcomplete에 없다면
                // 4
                productDao.postTradeReserved(productIdx, userIdx, reservationIdx);
            }
            // 5
            productDao.patchProductStatusComplete(productIdx, "Y");
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyPostImageStatus(PatchProductStatus patchProductStatus) throws BaseException {
        int checkUserId = productDao.checkUserIdx(patchProductStatus.getUserIdx());
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        int checkPostImageStatus = productDao.checkProductImageStatus(patchProductStatus.getProductIdx());
        if(checkPostImageStatus == 0){//이미 삭제됐는지
            throw new BaseException(MODIFY_FAIL_INVALID_POSTIMAGE);
        }
        int checkUserPost = productDao.checkUserProduct(patchProductStatus);

        if(checkUserPost == 0){//유저가 맞지 않으면
            throw new BaseException(MODIFY_FAIL_INVALID_USER_WISHLIST);
        }
        try{
            int result = productDao.modifyProductImageStatus(patchProductStatus);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_WISHLIST_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyOnePostImageStatus(PatchProductStatus patchProductStatus) throws BaseException {
        int checkUserId = productDao.checkUserIdx(patchProductStatus.getUserIdx());
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        int checkOnePostImageStatus = productDao.checkOneProductImageStatus(patchProductStatus.getImgIdx());
        if(checkOnePostImageStatus == 0){//이미 삭제됐는지
            throw new BaseException(MODIFY_FAIL_INVALID_POSTIMAGE);
        }
        int checkUserPost = productDao.checkUserProduct(patchProductStatus);
        if(checkUserPost == 0){//유저가 맞지 않으면
            throw new BaseException(MODIFY_FAIL_INVALID_USER_WISHLIST);
        }
        try{

            int result = productDao.modifyOneProductImageStatus(patchProductStatus);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_WISHLIST_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
