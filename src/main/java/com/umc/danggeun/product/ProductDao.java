package com.umc.danggeun.product;

import com.umc.danggeun.product.model.PostProductImgReq;
import com.umc.danggeun.product.model.PostProductReq;
import com.umc.danggeun.product.model.ProductSelectedRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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

    public int createProductImage(PostProductImgReq postProductImgReq){
        String createProductImageQuery = "insert into ProductImage (productIdx, imgUrl) VALUES (?, ?)";
        Object[] createProductImageParams = new Object[]{postProductImgReq.getProductIdx(), postProductImgReq.getImgUrl()};
        this.jdbcTemplate.update(createProductImageQuery, createProductImageParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    private int productIdx;
    private int sellerIdx;
    private int regionName;
    private int categoryIdx;
    private String title;
    private String content;
    private int price;
    private String time;
    private String status;
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
                "           end as time, P.status from Product P left join (select regionIdx, regionName from Region) as R on P.regionIdx = R.regionIdx where P.regionIdx IN ("+ selectProductQuery +") AND (P.status = 'Y' OR P.status = 'R') ORDER BY P.createdAt DESC;";
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
}
