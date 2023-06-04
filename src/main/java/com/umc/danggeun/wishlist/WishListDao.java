package com.umc.danggeun.wishlist;


import com.umc.danggeun.wishlist.model.PostWishListReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class WishListDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkUserIdx(int userIdx){
        String checkUserIdxQuery = "select exists(select phone from User where userIdx = ? && isDeleted = 'N')";
        int checkUserIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                checkUserIdxParams);
    }

    public int checkProductIdx(int productIdx){
        String checkPostIdQuery = "select exists(select * from Product where productIdx = ? && status != 'N')";
        int checkPostIdParams = productIdx;
        return this.jdbcTemplate.queryForObject(checkPostIdQuery,
                int.class,
                checkPostIdParams);
    }

    // 찜 상품 등록
    public int createWishList(PostWishListReq postWishListReq){
        String createWishListQuery = "insert into WishList (userIdx, productIdx) VALUES (?, ?)";
        Object[] createWishListParams = new Object[]{postWishListReq.getUserIdx(), postWishListReq.getProductIdx()};
        this.jdbcTemplate.update(createWishListQuery, createWishListParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
}
