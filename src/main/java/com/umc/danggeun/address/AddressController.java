package com.umc.danggeun.address;

import com.umc.danggeun.address.model.GetNearRegionListRes;
import com.umc.danggeun.address.model.GetRegionRes;
import com.umc.danggeun.address.model.PostAddressReq;
import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.config.BaseResponse;
import com.umc.danggeun.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.umc.danggeun.config.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private final AddressProvider addressProvider;
    @Autowired
    private final AddressService addressService;
    @Autowired
    private final JwtService jwtService;

    /**
     * 내 동네 추가
     * POST /address/{regionIdx}
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

    /**
     * 대표 동네 바꾸기
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PutMapping("/change/{regionIdx}")
    public BaseResponse<String> PutChangeAddress(@PathVariable("regionIdx") int regionIdx){

        if(regionIdx < 805){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }

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

            addressService.changeAddress(userIdByJwt, regionIdx);

            String result = "동네가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 동네 설정 범위 변경
     * [Patch] /address/{regionIdx}/{range}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{regionIdx}/{range}")
    public BaseResponse<String> PostChangeAddressRange(@PathVariable("regionIdx") int regionIdx, @PathVariable("range") int range){

        if(regionIdx < 1 || regionIdx > 804 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }
        if( range < 0 || range > 3){
            return new BaseResponse<>(PATCH_RANGE_RANGE_ERROR);
        }

        // 토큰 유효기간 파악
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
            addressService.patchAddressRange(userIdByJwt, regionIdx, range);

            String result = "범위가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 검색을 통해 동네 조회
     * [GET] /address?search={search}
     * @return BaseResponse<List<GetRegionRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetRegionRes>> getTownSearchBySearch(@RequestParam("search") String search) {
        try{
            List<GetRegionRes> getRegionRes = addressProvider.getRegionBySearch(search);
            return new BaseResponse<>(getRegionRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 현재 위치를 통해 주변 동네 조회
    @ResponseBody
    @GetMapping("/location")
    public BaseResponse<List<GetRegionRes>> getTownSearchByLocation(@RequestParam("regionIdx") int regionIdx) {
        if(regionIdx < 1 || regionIdx > 805 ){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }

        try{
            List<GetRegionRes> getTownRes = addressProvider.getRegionByLocation(regionIdx);
            return new BaseResponse<>(getTownRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 특정 동네의 range 별 근처 동네 리스트 반환
    @ResponseBody
    @GetMapping("/near")
    public BaseResponse<GetNearRegionListRes> getNearTownLiST(@RequestParam("regionIdx") int regionIdx) throws BaseException {

        if(regionIdx < 1 || regionIdx > 805){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }

        try{
            GetNearRegionListRes getNearTownListRes  = addressProvider.getNearRegionList(regionIdx);
            return new BaseResponse<>(getNearTownListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 동네 인증 추가하기
    @ResponseBody
    @PatchMapping("/auth/{regionIdx}")
    public BaseResponse<String> PatchCertificationAddress(@PathVariable("regionIdx") int regionIdx){

        if(regionIdx < 1 || regionIdx > 805){
            return new BaseResponse<>(GET_TOWN_EXIST_ERROR);
        }

        // 토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        int userIdxByJwt;

        try {
            userIdxByJwt = jwtService.getUserIdx();

            addressService.patchCertificationAddress(userIdxByJwt, regionIdx);
            String result = "동네가 인증되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
