package com.umc.danggeun.address;

import com.umc.danggeun.address.model.PostAddressReq;
import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.config.BaseResponse;
import com.umc.danggeun.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.umc.danggeun.config.BaseResponseStatus.GET_TOWN_EXIST_ERROR;
import static com.umc.danggeun.config.BaseResponseStatus.INVALID_JWT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {
//    final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    private final AddressProvider addressProvider;
    @Autowired
    private final AddressService addressService;
    @Autowired
    private final JwtService jwtService;

    /**
     * 내 동네 추가
     * [POST] /address/:townId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/{regionIdx}")
    public BaseResponse<String> PostAddress(@RequestBody PostAddressReq postAddressReq, @PathVariable("regionIdx") int regionIdx){

        if(regionIdx < 1 || regionIdx > 6561 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }

//        //토큰 유효기간 파악
//        try {
//            Date current = new Date(System.currentTimeMillis());
//            if(current.after(jwtService.getExp())){
//                throw new BaseException(INVALID_JWT);
//            }
//        }catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }

//        int userIdByJwt;
        try {
//            userIdByJwt = jwtService.getUserId();
            int userIdx = postAddressReq.getUserIdx();

            addressService.postAddress(userIdx, regionIdx); // postAddress(userIdByJwt, regionIdx);

            String result = "동네가 추가되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 내 동네 삭제
     * [DELETE] /address/{regionIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{regionIdx}")
    public BaseResponse<String> DeleteAddress(@PathVariable("regionIdx") int regionIdx){

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        int userIdByJwt;
        try {

            userIdByJwt = jwtService.getUserIdx();

            addressService.deleteAddress(userIdByJwt, regionIdx);

            String result = "동네가 삭제 되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
//
//    /**
//     * 내 동네 바꾸기
//     * [Patch] /address/change/:townId
//     * @return BaseResponse<String>
//     */
//
//    @ResponseBody
//    @PatchMapping("/change/{townId}")
//    public BaseResponse<String> PostChangeAddress(@PathVariable("townId") int townId){
//
//        if(townId < 1 || townId >6561 ){
//            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
//        }
//
//        //토큰 유효기간 파악
//        try {
//            Date current = new Date(System.currentTimeMillis());
//            if(current.after(jwtService.getExp())){
//                throw new BaseException(INVALID_JWT);
//            }
//        }catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//
//        int userIdByJwt;
//        try {
//            userIdByJwt = jwtService.getUserId();
//
//
//            addressService.patchChangeAddress(userIdByJwt, townId);
//
//
//            String result = "동네가 변경되었습니다.";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception){
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
//
//
//
//    /**
//     * 유저가 설정한 townId, 인증여부, 범위 가져오기
//     * [GET] /address
//     * @return BaseResponse<GetAddressRes>
//     */
//    @ResponseBody
//    @GetMapping("/info")
//    public BaseResponse<GetAddressRes> getAddress() throws BaseException {
//
//        //토큰 유효기간 파악
//        try {
//            Date current = new Date(System.currentTimeMillis());
//            if(current.after(jwtService.getExp())){
//                throw new BaseException(INVALID_JWT);
//            }
//        }catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//
//        int userIdByJwt;
//        try {
//            userIdByJwt = jwtService.getUserId();
//
//            GetAddressRes getAddressRes = addressProvider.getAddress(userIdByJwt);
//
//            return new BaseResponse<>(getAddressRes);
//        } catch (BaseException exception){
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
}
