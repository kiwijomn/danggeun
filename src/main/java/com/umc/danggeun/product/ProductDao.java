package com.umc.danggeun.product;

import com.umc.danggeun.product.model.PatchProductStatus;
import com.umc.danggeun.product.model.PostProductImgReq;
import com.umc.danggeun.product.model.PostProductReq;
import com.umc.danggeun.product.model.ProductSelectedRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static com.umc.danggeun.utils.ValidationRegex.isRegexImage;

@RequiredArgsConstructor
@Repository
public class ProductDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createProduct(PostProductReq postProductReq){
        String createProductQuery = "insert into Product(sellerIdx, regionIdx, categoryIdx, title, content, price, proposal, share) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] createProductParams = new Object[]{postProductReq.getSellerIdx(), postProductReq.getRegionIdx(), postProductReq.getCategoryIdx(), postProductReq.getTitle(), postProductReq.getContent(), postProductReq.getPrice(), postProductReq.getProposal(), postProductReq.getShare()};
        this.jdbcTemplate.update(createProductQuery, createProductParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    @Transactional
    public int[] createProductImage(PostProductImgReq postProductImgReq){
        int[] insertedImgIdx = new int[postProductImgReq.getImgUrl().length];
        for (int i = 0; i < postProductImgReq.getImgUrl().length; i++) {
            if (isRegexImage(postProductImgReq.getImgUrl()[i])) {
                String createProductImageQuery = "insert into ProductImage (productIdx, imgUrl) VALUES (?, ?)";
                Object[] createProductImageParams = new Object[]{postProductImgReq.getProductIdx(), postProductImgReq.getImgUrl()[i]};
                this.jdbcTemplate.update(createProductImageQuery, createProductImageParams);
                String lastInsertIdQuery = "select last_insert_id()";
                int insertedId = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
                insertedImgIdx[i] += insertedId;
            }
        }
        return insertedImgIdx;
    }

    public List<ProductSelectedRes> getProductUseAddress(String selectProductQuery){ //, String interestCategoryQurey
        String getProductUseAddressQuery = "select P.productIdx, P.sellerIdx, R.regionName, P.categoryIdx, P.title, P.content, P.price, case" +
                "           when TIMESTAMPDIFF(second, P.createdAt, current_timestamp) < 60\n" +
                "               then concat(TIMESTAMPDIFF(second, P.createdAt, current_timestamp), '초 전') # 1분 미만에는 초로 표시\n" +
                "           when TIMESTAMPDIFF(minute, P.createdAt, current_timestamp) < 60\n" +
                "               then concat(TIMESTAMPDIFF(minute, P.createdAt, current_timestamp), '분 전') # 1시간 미만에는 분으로 표시\n" +
                "           when TIMESTAMPDIFF(hour, P.createdAt, current_timestamp) < 24\n" +
                "               then concat(TIMESTAMPDIFF(hour, P.createdAt, current_timestamp), '시간 전') # 24시간 미만에는 시간으로 표시\n" +
                "           when TIMESTAMPDIFF(DAY, P.createdAt, current_timestamp) < 31\n" +
                "               then concat(TIMESTAMPDIFF(DAY, P.createdAt, current_timestamp), '일 전') # 31일 미만에는 일로 표시\n" +
                "            when TIMESTAMPDIFF(MONTH, P.createdAt, current_timestamp) < 12\n" +
                "               then concat(TIMESTAMPDIFF(MONTH, P.createdAt, current_timestamp), '개월 전') # 12월 미만에는 월로 표시\n" +
                "           when TIMESTAMPDIFF(YEAR, P.createdAt, current_timestamp) >= 1\n" +
                "               then concat(TIMESTAMPDIFF(YEAR, P.createdAt, current_timestamp), '년 전') # 1년 이상은 년으로 표시\n" +
                "\n" +
                "           end as time, P.status from Product P left join (select regionIdx, regionName from Region) as R on P.regionIdx = R.regionIdx where P.regionIdx IN ("+ selectProductQuery +") AND (P.status = 'Y' OR P.status = 'R') ORDER BY P.createdAt DESC LIMIT 0, 3;";
        return this.jdbcTemplate.query(getProductUseAddressQuery,
                (rs, rowNum) -> new ProductSelectedRes(
                        rs.getInt("productIdx"),
                        rs.getInt("sellerIdx"),
                        rs.getString("regionName"),
                        rs.getInt("categoryIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("price"),
                        rs.getString("time"),
                        rs.getString("status"))
        );
    }

    // Trade
    public int checkProductDeleted(int productIdx){
        String checkProductDeletedQuery = "select exists(select * from product where productIdx = ? && status='D')";
        int checkProductDeletedParams = productIdx;
        return this.jdbcTemplate.queryForObject(checkProductDeletedQuery,
                int.class,
                checkProductDeletedParams);
    }

    public int getUserIdxByProductIdx(int productIdx){
        String getUserIdxByProductQuery = "select userIdx from product where productIdx = ?";
        int getUserIdxByProductParams = productIdx;
        return this.jdbcTemplate.queryForObject(getUserIdxByProductQuery,
                int.class,
                getUserIdxByProductParams);
    }

    public int checkTradeComplete(int productIdx){
        String checkTradeCompleteQuery = "select exists(select * from Trade where productIdx = ? && status = 'C')";
        int checkTradeCompleteParams = productIdx;
        return this.jdbcTemplate.queryForObject(checkTradeCompleteQuery,
                int.class,
                checkTradeCompleteParams);
    }

    // 이미 status가 Complete로 되어 있는 경우 해당 거래 튜플의 판매자와 구매자 유저를 변경
    public void setTradeUserComplete(int productIdx, int userIdx, int buyerIdx){
        String setTradeUserCompleteQuery = "update Trade set sellerIdx = ?, buyerIdx = ? where productIdx = ? ";
        Object[] setTradeUserCompleteParams = new Object[]{userIdx, buyerIdx, productIdx};

        this.jdbcTemplate.update(setTradeUserCompleteQuery, setTradeUserCompleteParams);
    }

    // 해당 product에 대해 status가 Complete인 튜플이 존재하지 않는다면 삽입
    public void postTradeComplete(int productIdx, int userIdx, int buyerIdx){
        String postTradeCompleteQuery = "insert into Trade (productIdx, sellerIdx, buyerIdx, status) VALUES (?,?,?, 'C')";
        Object[] postTradeCompleteParams = new Object[]{productIdx, userIdx, buyerIdx};
        this.jdbcTemplate.update(postTradeCompleteQuery, postTradeCompleteParams);
    }

    public void patchProductStatusComplete(int productIdx, String status){
        String patchPostStatusCompleteQuery = "update Product set status = ? where productIdx = ?";
        Object[] patchPostStatusCompleteParams = new Object[]{status, productIdx};

        this.jdbcTemplate.update(patchPostStatusCompleteQuery, patchPostStatusCompleteParams);
    }

    public void patchTradeCompleteToSale(int productIdx){
        String patchTradeCompleteToSaleQuery = "update Trade set status = 'S' where productIdx = ? ";
        Object[] patchTradeCompleteToSaleParams = new Object[]{ productIdx };

        this.jdbcTemplate.update(patchTradeCompleteToSaleQuery, patchTradeCompleteToSaleParams);

    }

    public void patchTradeCompleteToReserved(int productIdx, int userIdx, int reservationIdx){
        String patchTradeCompleteToReservedQuery = "update Trade set status = 'R', sellerIdx = ?, reservationIdx = ? where productIdx = ? ";
        Object[] patchTradeCompleteToReservedParams = new Object[]{userIdx, reservationIdx, productIdx };

        this.jdbcTemplate.update(patchTradeCompleteToReservedQuery, patchTradeCompleteToReservedParams);
    }

    public void postTradeReserved(int productIdx, int userIdx, int reservationIdx){
        String postDealCompleteQuery = "insert into Trade (productIdx, sellerIdx, reservationIdx, status) VALUES (?,?,?, 'R')";
        Object[] postDealCompleteParams = new Object[]{productIdx, userIdx, reservationIdx};
        this.jdbcTemplate.update(postDealCompleteQuery, postDealCompleteParams);
    }

    //유저아이디로 유저가 존재하는지
    public int checkUserIdx(int userIdx){
        String checkUserIdxQuery = "select exists(select phone from User where userIdx = ? && isDeleted='N')";
        int checkUserIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                checkUserIdxParams);
    }

    //게시글 특정 이미지 삭제
    public int modifyOneProductImageStatus(PatchProductStatus patchProductStatus){
        String modifyOnePostImageStatusQuery = "update ProductImage set isDeleted = ? where imgIdx = ? ";
        Object[] modifyOnePostImageStatusParams = new Object[]{patchProductStatus.getStatus(), patchProductStatus.getImgIdx()};

        return this.jdbcTemplate.update(modifyOnePostImageStatusQuery,modifyOnePostImageStatusParams);
    }

    //게시글 인덱스로 이미지 존재하는지
    public int checkProductImageStatus(int productIdx){
        String checkPostImageQuery = "select exists(select * from ProductImage where productIdx = ? && isDeleted='N')";
        int checkPostImageParams = productIdx;
        return this.jdbcTemplate.queryForObject(checkPostImageQuery,
                int.class,
                checkPostImageParams);
    }

    //게시글 이미지 인덱스로 특정 이미지 존재하는지
    public int checkOneProductImageStatus(int imgIdx){
        String checkOnePostImageQuery = "select exists(select * from ProductImage where imgIdx = ? && isDeleted='N')";
        int checkOnePostImageParams = imgIdx;
        return this.jdbcTemplate.queryForObject(checkOnePostImageQuery,
                int.class,
                checkOnePostImageParams);
    }

    //유저의 관심목록이 맞는지 체크
    public int checkUserProduct(PatchProductStatus patchProductStatus){
        String checkUserProductQuery = "select exists(select * from Product where userIdx = ? && productIdx = ?)";
        Object[] checkUserProductParams = new Object[]{patchProductStatus.getUserIdx(), patchProductStatus.getProductIdx()};
        return this.jdbcTemplate.queryForObject(checkUserProductQuery,
                int.class,
                checkUserProductParams);
    }

    //게시글 전체 이미지 삭제
    public int modifyProductImageStatus(PatchProductStatus patchProductStatus){
        String modifyPostImageStatusQuery = "update ProductImage set status = ? where productIdx = ? ";
        Object[] modifyPostImageStatusParams = new Object[]{patchProductStatus.getStatus(), patchProductStatus.getProductIdx()};

        return this.jdbcTemplate.update(modifyPostImageStatusQuery,modifyPostImageStatusParams);
    }
}
