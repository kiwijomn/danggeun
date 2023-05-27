package com.umc.danggeun.user;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.user.model.PatchUserProfileReq;
import com.umc.danggeun.user.model.PostUserReq;
import com.umc.danggeun.user.model.PostUserRes;
import com.umc.danggeun.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Random;

import static com.umc.danggeun.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 이미 등록된 번호인지 확인
        if(userDao.checkPhoneNumber(postUserReq.getPhone()) == 1){
            throw new BaseException(POST_USERS_DUPLICATE_PHONENUMBER);
        }
        try{
            int regionIdx = userDao.getRegionIdx(postUserReq.getRegionName());

//            int leftLimit = 48; // numeral '0'
//            int rightLimit = 57; // letter '9'
//            int targetStringLength = 6;
//            Random random = new Random();
//            String certificationNum = random.ints(leftLimit,rightLimit + 1)
//                    .limit(targetStringLength)
//                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                    .toString();

            // User table insert
            int userIdx = userDao.createUser(postUserReq);

            // Address table insert
            userDao.createAddress(userIdx, regionIdx);

            // to be updated: jwt 발급 process
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(userIdx, jwt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // PATCH
    public void patchUserProfile(int userIdx, PatchUserProfileReq patchUserProfileReq) throws BaseException {
        if (userDao.checkUserExist(userIdx) == 0) { // 유효한 user인지 확인
            throw new BaseException(POST_POST_INVALID_USER);
        }
        // 닉네임을 포함해서 프로필 수정할 경우
        if(!userDao.checkUserNickname(userIdx).equals(patchUserProfileReq.getNickname())){ // 닉네임이 다른지 확인
            if(userDao.checkNicknameUpdated(userIdx) < 30){ // 닉네임 변경한 지 30일 이후인지 확인
                throw new BaseException(NICKNAME_UPDATED_ERROR) ;
            }
            userDao.patchUserProfile(userIdx, patchUserProfileReq); // 유저 정보 수정
        }else{ // 닉네임 수정 안 할 경우
            userDao.patchUserProfileImage(userIdx, patchUserProfileReq.getProfileImg()); // 유저 정보 수정
        }
    }
}
