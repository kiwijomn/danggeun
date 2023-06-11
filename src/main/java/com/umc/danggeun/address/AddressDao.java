package com.umc.danggeun.address;

import com.umc.danggeun.address.model.GetLocation;
import com.umc.danggeun.address.model.GetRegionRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AddressDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int countUserAddress(int userIdx){
        String countUserAddressQuery = "select count(*) from Address where userIdx = ? and isDeleted = 'N'";
        return this.jdbcTemplate.queryForObject(countUserAddressQuery,
                int.class,
                userIdx
        );
    }

    public void patchMainTown(int userIdx){
        String patchMainTownQuery = "update Address set isMain = 'N' where userIdx = ? and isMain = 'Y'";
        Object[] patchMainTownParams = new Object[]{userIdx};

        this.jdbcTemplate.update(patchMainTownQuery, patchMainTownParams);
    }

    public int getIsExistAddress(int userIdx, int regionIdx){
        String getIsExistAddressQuery = "select exists(select addressIdx from Address where userIdx = ? and regionIdx = ?)";
        return this.jdbcTemplate.queryForObject(getIsExistAddressQuery,
                int.class,
                userIdx, regionIdx
        );
    }

    public int getAddressIdx(int userIdx, int regionIdx){
        String getAddressIdQuery = "select addressIdx from Address where userIdx = ? and regionIdx = ?";
        return this.jdbcTemplate.queryForObject(getAddressIdQuery,
                int.class,
                userIdx, regionIdx
        );
    }

    public void patchAddressRange(int addressIdx, int range){
        String patchAddressRangeQuery = "update Address set `range` = ? where addressIdx = ?";
        Object[] patchAddressRangeParams = new Object[]{range, addressIdx};

        this.jdbcTemplate.update(patchAddressRangeQuery,patchAddressRangeParams);
    }

    public void patchAddressIsValid(int addressIdx){
        String patchAddressIsValidQuery = "update Address set isDeleted = 'N', isMain = 'Y' where addressIdx = ?";
        Object[] patchAddressIsValidParams = new Object[]{addressIdx};

        this.jdbcTemplate.update(patchAddressIsValidQuery, patchAddressIsValidParams);
    }

    public void createAddress(int userIdx, int regionIdx){
        String createAddressQuery = "insert into Address (userIdx, regionIdx) VALUES (?, ?)";
        Object[] createUserParams = new Object[]{userIdx, regionIdx};
        this.jdbcTemplate.update(createAddressQuery, createUserParams);
    }

    public int isSelectedRegion(int userIdx, int regionIdx){
        String getIsSelectedRegionQuery = "select exists(select addressIdx from Address where userIdx = ? and regionIdx = ? and isDeleted = 'N')";
        return this.jdbcTemplate.queryForObject(getIsSelectedRegionQuery,
                int.class,
                userIdx, regionIdx
        );
    }

    public void updateAddressIsDeleted(int addressIdx){
        String updateAddressIsDeletedQuery = "update Address set isDeleted = 'Y', isMain ='N' where addressIdx = ?";
        Object[] updateAddressIsDeletedParams = new Object[]{addressIdx};

        this.jdbcTemplate.update(updateAddressIsDeletedQuery, updateAddressIsDeletedParams);
    }

    public int getExistAddressIdx(int userIdx){
        String getExistAddressIdxQuery = "select addressIdx from Address where userIdx = ?  and isDeleted = 'N'";
        return this.jdbcTemplate.queryForObject(getExistAddressIdxQuery,
                int.class,
                userIdx
        );
    }

    public GetLocation getLocation(int regionIdx){
        String getLocationQuery = "select latitude, longitude from Region where regionIdx = ? ";
        return this.jdbcTemplate.queryForObject(getLocationQuery,
                (rs, rowNum) -> new GetLocation(
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("longitude")
                ),
                regionIdx);
    }

    public ArrayList<Integer> getNearRegionListByRange(int range, BigDecimal latitude, BigDecimal longitude){
        String getNearRegionIdQuery = "SELECT regionIdx\n" +
                "FROM Region\n" +
                "where ((6371 * acos(cos(radians(?)) * cos(radians(latitude)) *\n" +
                "                   cos(radians(longitude) - radians(?)) +\n" +
                "                   sin(radians(?)) * sin(radians(latitude)))) < ?)";

        ArrayList<Integer> list = new ArrayList<>();

        this.jdbcTemplate.query(getNearRegionIdQuery,
                (rs, rowNum) -> list.add(rs.getInt("regionIdx")),
                latitude, longitude, latitude, range);
        return list;
    }

    public void patchAddressIsMain(int addressIdx){
        String updateAddressIsMainQuery = "update Address set isDeleted = 'N', isMain = 'Y' where addressIdx = ?";
        Object[] updateAddressIsMainParams = new Object[]{addressIdx};
        this.jdbcTemplate.update(updateAddressIsMainQuery, updateAddressIsMainParams);
    }

    public List<GetRegionRes> getRegionBySearch(String search){
        String getRegionBySearchQuery = "select city, district, regionName from Region\n" +
                "where regionName like concat('%'," + search.trim() + ", '%')";
        return this.jdbcTemplate.query(getRegionBySearchQuery,
                (rs, rowNum) -> new GetRegionRes(
                        rs.getString("city"),
                        rs.getString("district"),
                        rs.getString("regionName")
                ));
    }

    public List<GetRegionRes> getNearRegionOrderByName(BigDecimal latitude, BigDecimal longitude){
        String getNearTownOrderByName =
                "select regionIdx, city, district, regionName \n" +
                        "from Region\n" +
                        "group by city, district, regionName, latitude, longitude\n" +
                        "order by (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) +\n" +
                        "                      sin(radians(?)) * sin(radians(latitude))))\n" +
                        "limit 50";

        return this.jdbcTemplate.query(getNearTownOrderByName,
                (rs, rowNum) -> new GetRegionRes(
                        rs.getString("city"),
                        rs.getString("district"),
                        rs.getString("regionName")
                ),
                latitude, longitude, latitude);
    }

    public int isSelectedAddress(int userIdx, int regionIdx) {
        String getIsSelectedAddressQuery = "select exists(select addressIdx from Address where userIdx = ? and regionIdx = ? and isMain = 'Y')";
        return this.jdbcTemplate.queryForObject(getIsSelectedAddressQuery,
                int.class,
                userIdx, regionIdx
        );
    }

    public void patchAddressIsAuth(int addressIdx){
        String patchAddressIsAuthQuery = "update Address set isAuth = 'Y' where addressIdx = ? ";
        Object[] patchAddressIsAuthParams = new Object[]{addressIdx};

        this.jdbcTemplate.update(patchAddressIsAuthQuery, patchAddressIsAuthParams);

        // update 시간 바꾸기
        String patchIsAuthUpdatedQuery = "update Address set isAuthUpdated = CURRENT_TIMESTAMP where addressIdx = ? ";
        Object[] patchIsAuthUpdatedParams = new Object[]{addressIdx};

        this.jdbcTemplate.update(patchIsAuthUpdatedQuery, patchIsAuthUpdatedParams);
    }
}
