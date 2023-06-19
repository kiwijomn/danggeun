package com.umc.danggeun.user;

import com.umc.danggeun.config.BaseException;
import com.umc.danggeun.user.model.GetUserRes;
import com.umc.danggeun.user.model.PostLoginReq;
import com.umc.danggeun.user.model.PostLoginRes;
import com.umc.danggeun.user.model.User;
import com.umc.danggeun.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.umc.danggeun.config.BaseResponseStatus.DATABASE_ERROR;
import static com.umc.danggeun.config.BaseResponseStatus.POST_POST_INVALID_USER;

@Service
@RequiredArgsConstructor
public class UserProvider {
    private final UserDao userDao;
    private final JwtService jwtService;

//    final Logger logger = LoggerFactory.getLogger(this.getClass());

    //유저 상태 체크
    public int checkStatus(String phone) throws BaseException {
        try{
            return userDao.checkStatus(phone);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException{
        try{
            User user = userDao.getUsersByPhone(postLoginReq);
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserRes getUser(int userIdx) throws BaseException{
        if (userDao.checkUserExist(userIdx) == 0) { // user의 상태가 isDeleted 인지 확인
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{ // 유효한 user일 경우 정보 조회
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
