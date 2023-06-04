package com.umc.danggeun.address;

import com.umc.danggeun.config.BaseException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.umc.danggeun.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class AddressService {
//    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AddressDao addressDao;
//    private final AddressProvider addressProvider;
//    private final JwtService jwtService;

    public void postAddress(int userIdx, int regionIdx) throws BaseException {

        // 1. 현재 설정된 동네 갯수 파악 -> 현재 설정된 2개 미만이라면 동네 추가 가능
        // 2. 이미 저장되어있었던 동네인 지 확인
        // 3. 저장되어있다면 status Valid로 바꿈
        // 4. 저장되어있지 않다면

        // 1. 현재 설정된 동네 갯수 파악 -> 현재 설정된 2개 미만이라면 동네 추가 가능
        // 2. 이미 저장되어있었던 동네인 지 확인
        // 3. 저장되어있다면 status Valid로 바꿈
        // 4. 저장되어있지 않다면


        // 1. 현재 설정된 동네 갯수 파악
        int ValidTownNum = addressDao.countUserAddress(userIdx); // 현재 설정된 동네 개수 파악
        if (ValidTownNum >= 2) { // 동네가 2개면 추가 불가
            throw new BaseException(CREATE_ADDRESS_ERROR);
        }

        // 1-1 현재 설정된 동네가 존재할 때 -> 현재 설정된 동네의 mainTown를 Invalid로 바꿔야함
        if (ValidTownNum == 1) {
            try {
                addressDao.patchMainTown(userIdx);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }

        try {
            // 2. 이미 저장되어있던 동네라면
            if (addressDao.getIsExistAddress(userIdx, regionIdx) == 1) { // 이미 등록한 적이 있던 동네라면
                //3-1. addressId 가져오기
                int addressIdx = addressDao.getAddressIdx(userIdx, regionIdx); // addressIdx를 가져와서
                addressDao.patchAddressIsValid(addressIdx); // 해당 튜플의 isDeleted를 N으로, isMain을 Y로 바꾸기
            }
            // 4. 저장되어있지 않은 동네라면
            else {
                //4-1. 새로 생성하기
                addressDao.createAddress(userIdx, regionIdx);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteAddress(int userIdx, int regionIdx) throws BaseException {

        // 1. address에 userId 와 townId가 존재하는 행이 Valid 상태인지 확인 (현재 선택된 동네인지 확인)
        // 2. 1이 확인된다면 status = inValid, mainTown = Invalid 상태로 바꾸기

        //1. 현재 선택된 상태의 동네인지 확인
        if (addressDao.isSelectedRegion(userIdx ,regionIdx) == 0) {
            throw new BaseException(DELETE_ADDRESS_EXIST_ERROR);
        }

        //2. status = Invalid, mainTown =Invalid 로 수정
        try{
            int addressIdx = addressDao.getAddressIdx(userIdx, regionIdx); // addressIdx 추출

            //3-2. addressId에 해당하는 status Invalid, mainTown = Invalid로 바꾸기
            addressDao.updateAddressIsDeleted(addressIdx); // 해당 튜플의 isDeleted 필드를 Y로, isMain 필드를 N으로 update
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        try {
            // 현재 선택된 동네가 남아있다면
            int existAddress = addressDao.countUserAddress(userIdx);
            if (existAddress == 1) {
                // 삭제되지 않은 동네(= 남은 동네) isMain = Y로 update
                int addressIdx = addressDao.getExistAddressIdx(userIdx);
                addressDao.patchAddressIsValid(addressIdx);
            }
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // main 동네 변경
    public void changeAddress(int userIdx, int regionIdx) throws BaseException{
        // 1. 현재 유저가 선택한 동네가 2개인지 확인
        // 2. 현재 유저가 townId를 선택하고 있는지 확인
        // 3. 현재 선택한 동네의 mainTown = Invalid 로 변경
        // 4. townId가 해당되는 adrress 행의 mainTown = Valid 로 변경

        // 현재 유저가 선택한 동네의 개수
        int validRegionNum = addressDao.countUserAddress(userIdx);
        if (validRegionNum != 2) { // 2개이면 change x
            throw new BaseException(POST_ADDRESS_CHANGE_ERROR);
        }

        // region을 선택했는지 확인
        if (addressDao.isSelectedRegion(userIdx, regionIdx) == 0) {
            throw new BaseException(POST_ADDRESS_EXIST_ERROR);
        }

        try{
            // 현재 선택한 동네의 isMain을 N으로
            addressDao.patchMainTown(userIdx);

            // addressIdx에 해당하는 isMain을 Y로 설정
            int addressIdx = addressDao.getAddressIdx(userIdx, regionIdx);
            addressDao.patchAddressIsMain(addressIdx);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void patchAddressRange(int userIdx, int regionIdx, int range) throws BaseException {
        // 1. 현재 유저가 regionIdx를 선택하고 있는지 확인
        // 2. addressIdx 찾기
        // 3. addressIdx의 range 변경

        if (addressDao.isSelectedRegion(userIdx, regionIdx) == 0) {
            throw new BaseException(POST_ADDRESS_EXIST_ERROR);
        }
        try {
            int addressIdx = addressDao.getAddressIdx(userIdx, regionIdx);
            addressDao.patchAddressRange(addressIdx, range);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
