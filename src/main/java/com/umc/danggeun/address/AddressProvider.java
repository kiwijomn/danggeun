package com.umc.danggeun.address;

import com.umc.danggeun.address.model.GetLocation;
import com.umc.danggeun.address.model.GetNearRegionListRes;
import com.umc.danggeun.config.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.umc.danggeun.config.BaseResponseStatus.DATABASE_ERROR;
import static com.umc.danggeun.config.BaseResponseStatus.DATABASE_ERROR2;

@Service
@RequiredArgsConstructor
public class AddressProvider {
    private final AddressDao addressDao;

    public GetNearRegionListRes getNearRegionList(int regionIdx) throws BaseException {
        // 1. townId의 lat와 lng를 구한다.
        // 2. townId가 서울에 속하면 r1, r2, r3,r4 는 각각 1,2,3,4 km의 영역
        //    서울이 아니면 r1,r2,r3,r4는 각각 2,5,8,10km의 영역
        // 3. range별 townId List 만들기
        // 4. getNearTownListRes에 넣고 반환
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
}
