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
import static com.umc.danggeun.config.BaseResponseStatus.PATCH_WISHLIST_INVALID_STATUS;

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

    @ResponseBody
    @PatchMapping("/{productIdx}/complete/{buyerIdx}")
    public BaseResponse<String> patchPostComplete(@PathVariable("productIdx") int productIdx, @PathVariable("buyerIdx") int buyerIdx) {
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());

            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
            int userIdx = jwtService.getUserIdx();
            productService.patchProductComplete(productIdx, userIdx, buyerIdx);

            String result = "";
            return new BaseResponse<>(result);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{productIdx}/sale")
    public BaseResponse<String> patchPostSale(@PathVariable("productIdx") int productIdx) {
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
            int userIdx = jwtService.getUserIdx();
            productService.patchProductSale(productIdx, userIdx);

            String result = "";
            return new BaseResponse<>(result);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @PatchMapping("/{productIdx}/reserved/{reservationIdx}")
    public BaseResponse<String> patchPostReserved(@PathVariable("productIdx") int productIdx, @PathVariable("reservationIdx") int reservationIdx) {
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
            int userIdx = jwtService.getUserIdx();
            productService.patchProductReserved(productIdx, userIdx, reservationIdx);

            String result = "";
            return new BaseResponse<>(result);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/image/{productIdx}/status")
    public BaseResponse<String> modifyPostImageStatus(@PathVariable("productIdx") int productIdx, @RequestParam(required = false, defaultValue = "-1") int imgIdx, @RequestBody Product product){
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        try {
            //jwt에서 idx 추출.
            int userIdx = jwtService.getUserIdx();

            if(product.getStatus().equals("Invalid")){
                if(imgIdx != -1){
                    PatchProductStatus patchProductStatus = new PatchProductStatus(userIdx, productIdx, imgIdx, product.getStatus());
                    productService.modifyOnePostImageStatus(patchProductStatus);
                }
                else{
                    PatchProductStatus patchProductStatus = new PatchProductStatus(userIdx, productIdx, imgIdx, product.getStatus());
                    productService.modifyPostImageStatus(patchProductStatus);
                }

                String result = "";
                return new BaseResponse<>(result);
            }
            else{
                return new BaseResponse<>(PATCH_WISHLIST_INVALID_STATUS);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
