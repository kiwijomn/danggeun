package com.umc.danggeun.user;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.config.BaseResponse;
import com.umc.danggeun.user.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.umc.danggeun.config.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;

    // API 구현부
     /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("/signup")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // 회원가입 시 동네를 먼저 설정해두고 절차 진행, null 검사
        if(postUserReq.getRegionName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_ADDRESS);
        }
        if(postUserReq.getPhone() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }
        if(postUserReq.getNickname() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // 모든 데이터가 받아지면 createUser 실행
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq){
        try{
            //휴대폰번호 입력 체크
            if(postLoginReq.getPhone() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
            }
//            //인증번호 입력 체크
//            if(postLoginReq.getCertificationNum() == null){
//                return new BaseResponse<>(POST_USERS_EMPTY_CERTIFICATIONNUM);
//            }

            // 정상 상태 유저인지 체크
            // 정상 상태가 아니라면 -> 회원가입으로 유도
            int checkStatus = userProvider.checkStatus(postLoginReq.getPhone());
            if(checkStatus == 0){ // 정상 상태가 아닌 유저라면
                return new BaseResponse<>(POST_USERS_INVALID_USER);
            }
//            //인증번호가 일치하는지 체크
//            int checkCertificationNum = userProvider.checkCertificationNum(postLoginReq);
//            if(checkCertificationNum == 0){//정상 상태가 아닌 유저라면
//                return new BaseResponse<>(POST_USERS_INVALID_CERTIFICATIONNUM);
//            }
            PostLoginRes postLoginRes = userProvider.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저의 정보 조회
     * [GET] /user/{userIdx}
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUserAddress(@PathVariable("userIdx") int userIdx) throws BaseException {

        try {
            GetUserRes getUserRes = userProvider.getUser(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 프로필 수정
     * [Patch] /user/profile/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/profile/{userIdx}")
    public BaseResponse<String> patchUserProfile(@RequestBody PatchUserProfileReq patchUserProfileReq, @PathVariable("userIdx") int userIdx){

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

        try {
//            //권한 확인
//            userIdByJwt = jwtService.getUserId();
//            if(userIdByJwt != userIdx){
//                throw new BaseException(INVALID_USER_JWT);
//            }
            // 닉네임 길이 확인
            if(patchUserProfileReq.getNickname().length() < 2 || patchUserProfileReq.getNickname().length() > 12){
                throw new BaseException(NICKNAME_LENGTH_ERROR);
            }
            //프로필 수정
            userService.patchUserProfile(userIdx, patchUserProfileReq);
            String result = "유저 정보가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
