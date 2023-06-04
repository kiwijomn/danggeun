package com.umc.danggeun.product;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.config.BaseResponse;
import com.umc.danggeun.product.model.*;
import com.umc.danggeun.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.umc.danggeun.config.BaseResponseStatus.INVALID_JWT;

@RequestMapping("/product")
@RequiredArgsConstructor
@RestController
public class ProductController {
    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final JwtService jwtService;

    // 게시물 추가
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostProductRes> postProduct(@RequestBody PostProductReq postProductReq){
        try{
            PostProductRes postProductRes = productService.postProduct(postProductReq);
            return new BaseResponse<>(postProductRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 게시물 이미지 추가
    @ResponseBody
    @PostMapping("/image")
    public BaseResponse<PostProductImgRes> productImage(@RequestBody PostProductImgReq postProductImgReq){
        try{
            PostProductImgRes postProductImgRes = productService.productImage(postProductImgReq);
            return new BaseResponse<>(postProductImgRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<ProductSelectedRes>> getProudctUseAddress(@RequestParam("regionIdx") int regionIdx, @RequestParam("range") int range){
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        try{
            int userIdByJwt;
            userIdByJwt = jwtService.getUserIdx();
                List<ProductSelectedRes> getProductUseAddress = productProvider.getProductUseAddress(regionIdx, range);
                return new BaseResponse<>(getProductUseAddress);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
