package com.umc.danggeun.address;

import com.umc.danggeun.address.model.GetLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;

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

    public int getAddressId(int userIdx, int regionIdx){
        String getAddressIdQuery = "select addressIdx from Address where userIdx = ? and regionIdx = ?";
        return this.jdbcTemplate.queryForObject(getAddressIdQuery,
                int.class,
                userIdx, regionIdx
        );
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
}
