package com.umc.danggeun.product;


import com.umc.danggeun.address.AddressProvider;
import com.umc.danggeun.address.model.GetNearRegionListRes;
import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.product.model.PostProductReq;
import com.umc.danggeun.product.model.PostProductRes;
import com.umc.danggeun.product.model.ProductSelectedRes;
import com.umc.danggeun.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.umc.danggeun.config.BaseResponseStatus.DATABASE_ERROR;
import static com.umc.danggeun.config.BaseResponseStatus.DATABASE_ERROR3;

@Service
@RequiredArgsConstructor
public class ProductProvider {
    private final AddressProvider addressProvider;
    private final ProductDao productDao;
    private final JwtService jwtService;

    public List<ProductSelectedRes> getProductUseAddress(int regionIdx, int range) throws BaseException{

        try{ // range 값에 따라 다른 근처 동네를 조회해야 한다.
            GetNearRegionListRes getNearRegionListRes = addressProvider.getNearRegionList(regionIdx);
            List<Integer> rangeList = null;
            String selectProductQuery = "";

            if(range == 0){
                rangeList = getNearRegionListRes.getRange1();
            }
            else if(range == 1){
                rangeList = getNearRegionListRes.getRange2();
            }
            else if(range == 2){
                rangeList = getNearRegionListRes.getRange3();
            }
            else if(range == 3){
                rangeList = getNearRegionListRes.getRange4();
            }
            // 리스트에 담긴 동네에 해당하는 게시글을 조회
            for (int i = 0; i < rangeList.size(); i++) {
                if (i == rangeList.size() - 1) {
                    int Idx = rangeList.get(i);
                    selectProductQuery += Idx;
                } else {
                    int Idx = rangeList.get(i);
                    selectProductQuery += Idx + ",";
                }
            }

            List<ProductSelectedRes> getProductUseAddress = productDao.getProductUseAddress(selectProductQuery); // interestCategoryQurey
            return getProductUseAddress;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR3);
        }

    }
}
