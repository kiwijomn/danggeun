package com.umc.danggeun.user;

import com.umc.danggeun.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // DAO
    public int checkPhoneNumber(String phone){
        String checkStatusQuery = "select exists(select phone from User where phone = ? && isDeleted='N')";
        String checkStatusParams = phone;
        return this.jdbcTemplate.queryForObject(checkStatusQuery,
                int.class,
                checkStatusParams);
    }

    public int getRegionIdx(String regionName){
        String getRegionIdQuery = "select regionIdx from Region where regionName = ?";
        String checkStatusParams = regionName;
        return this.jdbcTemplate.queryForObject(getRegionIdQuery,
                int.class,
                checkStatusParams);
    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (nickname, phone, email) VALUES (?, ?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getNickname(), postUserReq.getPhone(), postUserReq.getEmail()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public void createAddress(int userIdx, int regionIdx){
        String createAddressQuery = "insert into Address (userIdx, regionIdx) VALUES (?, ?)";
        Object[] createAddressParams = new Object[] {userIdx, regionIdx};
        this.jdbcTemplate.update(createAddressQuery, createAddressParams);
    }

    // 유저 상태 조회 쿼리
    public int checkStatus(String phone){
        String checkStatusQuery = "select exists(select phone from User where phone = ? && isDeleted='N')";
        String checkStatusParams = phone;
        return this.jdbcTemplate.queryForObject(checkStatusQuery,
                int.class,
                checkStatusParams);
    }

    // 휴대폰 번호로 유저 조회
    public User getUsersByPhone(PostLoginReq postLoginReq){
        String getUsersByPhoneQuery = "select * from User where phone = ?";
        String getUsersByPhoneParams = postLoginReq.getPhone();
        return this.jdbcTemplate.queryForObject(getUsersByPhoneQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("phone"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("profileImg")),
                getUsersByPhoneParams);
    }

    // 중복 email 있는지 체크
    public int checkDuplicateEmail(String kakaoEmail){
        String checkDuplicateEmailQuery = "select exists(select userIdx from User where email = ?)";
        String checkDuplicateEmailParams = kakaoEmail;
        return this.jdbcTemplate.queryForObject(checkDuplicateEmailQuery, int.class,
                checkDuplicateEmailParams);
    }

    // 유저가 존재하는지 체크
    public int checkUserExist(int userIdx) {
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ? and isDeleted='N')";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select nickname, profileImg from User where userIdx = ? ";
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        userIdx,
                        rs.getString("nickname"),
                        rs.getString("profileImg")
                ),
                userIdx);
    }

    public String checkUserNickname(int userIdx){
        String checkUserNicknameQuery = "select nickname from User where userIdx = ? ";
        int checkUserNicknameParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserNicknameQuery,
                String.class,
                checkUserNicknameParams);
    }

    public int checkNicknameUpdated(int userIdx){
        String checkNicknameUpdatedQuery = "select DATEDIFF(CURDATE(), COALESCE(nicknameUpdatedAt, DATE_ADD(CURDATE(), INTERVAL -31 DAY))) from User where userIdx = ?";
        int checkNicknameUpdatedParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkNicknameUpdatedQuery,
                int.class,
                checkNicknameUpdatedParams);
    }

    public void patchUserProfile(int userIdx, PatchUserProfileReq patchUserProfileReq){
        String patchUserProfileQuery = "update User set nickname = ?, nicknameUpdatedAt = CURRENT_TIMESTAMP, profileImg = ? where userIdx = ? ";
        Object[] patchUserProfileParams = new Object[]{patchUserProfileReq.getNickname(), patchUserProfileReq.getProfileImg(), userIdx};

        this.jdbcTemplate.update(patchUserProfileQuery,patchUserProfileParams);
    }

    public void patchUserProfileImage(int userIdx, String profileImg){
        String patchUserProfileQuery = "update User set profileImg = ? where userIdx = ? ";
        Object[] patchUserProfileParams = new Object[]{profileImg, userIdx};

        this.jdbcTemplate.update(patchUserProfileQuery, patchUserProfileParams);
    }

//    public void createCategory(int userId){
//        for (int i = 1; i <= 18; i++) {
//            String createCategoryQuery = "insert into InterestedCategory (userIdx, categoryIdx) values (?, ?) ";
//            Object[] createCategoryParams = new Object[]{userId, i};
//
//            this.jdbcTemplate.update(createCategoryQuery, createCategoryParams);
//        }
//    }

}
