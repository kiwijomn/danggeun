package com.umc.danggeun.address;

import com.umc.danggeun.address.model.GetLocation;
import com.umc.danggeun.address.model.GetNearRegionListRes;
import com.umc.danggeun.address.model.GetRegionRes;
import com.umc.danggeun.config.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.umc.danggeun.config.BaseResponseStatus.DATABASE_ERROR;
import static com.umc.danggeun.config.BaseResponseStatus.DATABASE_ERROR2;

@Service
@RequiredArgsConstructor
public class AddressProvider {
    private final AddressDao addressDao;

    public GetNearRegionListRes getNearRegionList(int regionIdx) throws BaseException {
        // 1. regionIdx의 latitude와 longitude를 구한다.
        // 2. r1, r2, r3,r4 는 각각 1,2,3,4km의 영역
        // 3. range에 따라 regionIdx List 만들기
        // 4. getNearRegionListRes에 넣고 반환
        GetLocation getLoc;

        ArrayList<Integer> r1;
        ArrayList<Integer> r2;
        ArrayList<Integer> r3;
        ArrayList<Integer> r4;

        try {
            getLoc = addressDao.getLocation(regionIdx);
            r1 = addressDao.getNearRegionListByRange(1, getLoc.getLatitude(), getLoc.getLongitude());
            r2 = addressDao.getNearRegionListByRange(2, getLoc.getLatitude(), getLoc.getLongitude());
            r3 = addressDao.getNearRegionListByRange(3, getLoc.getLatitude(), getLoc.getLongitude());
            r4 = addressDao.getNearRegionListByRange(4, getLoc.getLatitude(), getLoc.getLongitude());

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR2);
        }

        GetNearRegionListRes getNearRegionListRes = new GetNearRegionListRes(r1, r2, r3, r4);
        return getNearRegionListRes;
    }

    public List<GetRegionRes> getRegionBySearch(String search) throws BaseException {
        List<GetRegionRes> getRegionRes;
        try {
            getRegionRes = addressDao.getRegionBySearch(search);
            return getRegionRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetRegionRes> getRegionByLocation(int regionIdx) throws BaseException {
        // 입력된 regionIdx를 이용하여 lat, lng 찾기
        GetLocation getLoc = addressDao.getLocation(regionIdx);

        // 현재 동네에서 가까운 순서대로 정렬한 리스트를 반환
        List<GetRegionRes> getRegionRes;
        try{
            getRegionRes = addressDao.getNearRegionOrderByName(getLoc.getLatitude(), getLoc.getLongitude());
            return getRegionRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
