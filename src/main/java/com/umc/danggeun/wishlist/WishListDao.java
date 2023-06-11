package com.umc.danggeun.wishlist;


import com.umc.danggeun.wishlist.model.PostWishListReq;
import com.umc.danggeun.wishlist.model.WishListCount;
import com.umc.danggeun.wishlist.model.WishListSelectRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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

    public List<WishListSelectRes> wishListSelect(int userIdx){
        String getWishListSelectResQuery = "select W.wishIdx, P.productIdx, P.regionIdx, P.title, P.categoryIdx, P.price, P.status\n" +
                "from WishList W\n" +
                "left join Product P on P.productIdx = W.productIdx\n" +
                "where W.userIdx = ? && W.isDeleted = 'N' && (P.status = 'Y' || P.status = 'N' || P.status = 'R');";
        return this.jdbcTemplate.query(getWishListSelectResQuery,
                (rs,rowNum) -> new WishListSelectRes(
                        rs.getInt("wishIdx"),
                        rs.getInt("productIdx"),
                        rs.getInt("regionIdx"),
                        rs.getString("title"),
                        rs.getInt("categoryIdx"),
                        rs.getInt("price"),
                        rs.getString("status")),
                userIdx
        );
    }

    // 특정 게시물 관심목록 개수 조회
    public WishListCount wishListCount(int productIdx){
        String getWishListCountQuery = "select count(*) as cnt from WishList where productIdx = ? && isDeleted = 'N'";
        return this.jdbcTemplate.queryForObject(getWishListCountQuery,
                (rs,rowNum) -> new WishListCount(
                        productIdx,
                        rs.getInt("cnt")),
                productIdx
        );
    }
}
