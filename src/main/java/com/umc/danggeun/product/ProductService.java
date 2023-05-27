package com.umc.danggeun.product;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.product.model.PostProductImgReq;
import com.umc.danggeun.product.model.PostProductImgRes;
import com.umc.danggeun.product.model.PostProductReq;
import com.umc.danggeun.product.model.PostProductRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.umc.danggeun.config.BaseResponseStatus.DATABASE_ERROR;

@RequiredArgsConstructor
@Service
public class ProductService {
//    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductDao productDao;
//    private final PostProvider postProvider;
//    private final JwtService jwtService;

    // 중고상품 게시글 작성
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
            int imgIdx = productDao.createProductImage(postProductImgReq);
            return new PostProductImgRes(imgIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
