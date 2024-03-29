package com.example.jatdauree.src.domain.app.store.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.app.basket.dto.GetStoreList;
import com.example.jatdauree.src.domain.app.review.dto.MyReviews;
import com.example.jatdauree.src.domain.app.store.dao.*;
import com.example.jatdauree.src.domain.app.store.dto.*;
import com.example.jatdauree.src.domain.app.subscribe.dao.AppSubscribeDao;
import com.example.jatdauree.src.domain.kakao.LocationValue;
import com.example.jatdauree.src.domain.web.review.dto.*;
import com.example.jatdauree.src.domain.web.review.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.jatdauree.config.BaseResponseStatus.*;

@Service
public class AppStoreService {
    private final AppStoreDao appStoreDao;
    private final ReviewDao reviewDao;
    private final AppSubscribeDao subscribeDao;

    private final LocationValue locationValue;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Autowired
    public AppStoreService(AppStoreDao appStoreDao, ReviewDao reviewDao, AppSubscribeDao subscribeDao, AmazonS3 s3Client) {
        this.appStoreDao = appStoreDao;
        this.reviewDao = reviewDao;
        this.subscribeDao = subscribeDao;
        this.locationValue = new LocationValue();
        this.s3Client = s3Client;
    }



    //쿼리 스트링 menu
    public GetAppStoreDetailMenuRes getAppStoreDetailMenu(int storeIdx) throws BaseException { //getAppStoreDetailMenu

        List<GetAppStoreDetailMenuItem> mainMenuList, sideMenuList;

        //메인메뉴조회
        try {
            mainMenuList = appStoreDao.getAppStoreDetailMenuList(storeIdx, "M");
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }
        // 메인 메뉴 url 가져오기
        try {
            for (GetAppStoreDetailMenuItem item : mainMenuList) {
                if (item.getMenuUrl() != null && !item.getMenuUrl().equals("")) {
                    item.setMenuUrl("" + s3Client.getUrl(bucketName, item.getMenuUrl()));
                }
            }
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }

        try {
            sideMenuList = appStoreDao.getAppStoreDetailMenuList(storeIdx, "S");
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }
        // 사이드 메뉴 url 가져오기
        try {
            for (GetAppStoreDetailMenuItem item : sideMenuList) {
                if (item.getMenuUrl() != null && !item.getMenuUrl().equals("")) {
                    item.setMenuUrl("" + s3Client.getUrl(bucketName, item.getMenuUrl()));
                }
            }
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }

        return new GetAppStoreDetailMenuRes(storeIdx, mainMenuList, sideMenuList);

    }

    //쿼리 스트링 info
    public GetAppStoreDetailInfoRes getAppStoreDetailInfo(int storeIdx) throws BaseException {
        // 1. 가게 정보
        // 상호명, 운영시간, 휴무일, 전화번호
        GetAppStoreDetailStoreInfo detailStoreInfo;
        try {
            detailStoreInfo = appStoreDao.getAppStoreDetailStoreInfo(storeIdx);
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }

        // 2. 가게 통계
        GetAppStoreDetailStatisticsInfo detailStatisticsInfo;
        try {
            int orderCount = appStoreDao.orderCount(storeIdx);
            int reviewCount = appStoreDao.reviewCount(storeIdx);
            int subscribeCount = appStoreDao.subscribeCount(storeIdx);
            detailStatisticsInfo =  new GetAppStoreDetailStatisticsInfo(orderCount,reviewCount, subscribeCount);

        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }

        // 3. 사업자 정보
        GetAppStoreDetailSellerInfo detailSellerInfo;
        try {
            detailSellerInfo = appStoreDao.getStoreAppDetailSellerInfo(storeIdx);
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }

        // 4. 원산지 정보
        String ingredientInfo;
        try {
            ingredientInfo = appStoreDao.getStoreAppDetailIngredientInfo(storeIdx);
        } catch (Exception e) {
            throw new BaseException(GET_MENU_ERROR);
        }

        return new GetAppStoreDetailInfoRes(storeIdx, detailStoreInfo,detailStatisticsInfo,detailSellerInfo,ingredientInfo);
    }

    //쿼리 스트링 review
    public GetAppStoreDetailReviewRes getAppStoreDetailReview(int storeIdx) throws BaseException { //getAppStoreDetailReview
        // 1. 리뷰 평균 평점 구하기
        GetAppReviewStarRes reviewStar;
        try {
            reviewStar = appStoreDao.reviewStarTotal(storeIdx);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        List<StarCountRatio> starCountRatios = new ArrayList<>();
        starCountRatios.add(new StarCountRatio("별 5개", reviewStar.getStar5(), (int) (reviewStar.getStar5() * 1.0 / reviewStar.getReviews_total() * 100)));
        starCountRatios.add(new StarCountRatio("별 4개", reviewStar.getStar4(), (int) (reviewStar.getStar4() * 1.0  / reviewStar.getReviews_total() * 100)));
        starCountRatios.add(new StarCountRatio("별 3개", reviewStar.getStar3(), (int) (reviewStar.getStar3() * 1.0  / reviewStar.getReviews_total() * 100)));
        starCountRatios.add(new StarCountRatio("별 2개", reviewStar.getStar2(), (int) (reviewStar.getStar2() * 1.0  / reviewStar.getReviews_total() * 100)));
        starCountRatios.add(new StarCountRatio("별 1개", reviewStar.getStar1(), (int) (reviewStar.getStar1() * 1.0  / reviewStar.getReviews_total() * 100)));

        // 2. 리뷰 목록 조회 및 url 가져오기
        List<AppReviewItems> reviewItems;
        try {
            reviewItems = appStoreDao.reviewItems(storeIdx);
            for (AppReviewItems item : reviewItems) {
                if (item.getReview_url() != null)
                    item.setReview_url("" + s3Client.getUrl(bucketName, item.getReview_url()));
                item.setOrderTodayMenu(appStoreDao.appOrderTodayMenus(item.getOrderIdx()));
            }
        } catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }

        try{
            LocalDateTime now = LocalDateTime.now(); //현재 시간
            for(AppReviewItems reviews : reviewItems){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //DAO에서 받아오는 getDate와 형식을 맞추어 준다.
                LocalDateTime reviewDate = LocalDateTime.parse(reviews.getDate(),formatter);

                Duration duration = Duration.between(reviewDate, now); // 현재 시간과 DB에서 가져온 getDate 사이의 시간

                long years = duration.toDays() / 365;
                long months = duration.toDays() / 30;
                long days = duration.toDays();
                long hours = duration.toHours();
                long minutes = duration.toMinutes();

                if (years > 0) {
                    reviews.setDate(years + "년 전");
                } else if (months > 0) {
                    reviews.setDate(months + "개월 전");;
                } else if (days > 0) {
                    reviews.setDate(days + "일 전");
                } else if (hours > 0) {
                    reviews.setDate(hours + "시간 전");
                } else if (minutes > 0) {
                    reviews.setDate(minutes + "분 전");
                } else {
                    reviews.setDate("방금 전");
                }
            }
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR); //날짜를 잘못 출력했습니다.
        }

        return new GetAppStoreDetailReviewRes(storeIdx,
                reviewStar.getStar_average(),
                reviewStar.getReviews_total(),
                reviewStar.getComment_total(),
                starCountRatios,
                reviewItems);
    }


    public List<StoreListXY> getStoreListByAddr(double maxX, double maxY, double minX, double minY) throws BaseException {
        //{minX, maxX, minY, maxY}
        double[] aroundXY = new double[]{minX, maxX, minY, maxY};

        // 주변 반경 내 현재 좌표로 가게 조회
        try{
            List<StoreListXY> storeListXY = appStoreDao.getStoreListByAddr(aroundXY);
            return storeListXY;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<StorePreviewRes> getStorePreview(int customerIdx, int storeIdx, Double longitude, Double latitude) throws BaseException{
        // 2. 선택한 가게 미리보기 정보 일단 가져오기
        StorePreviewDetails storePreview;
        try{
            storePreview = appStoreDao.getStorePreview(storeIdx);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        // 선택한 가게의 좌표
        double seletX = storePreview.getX();
        double selectY = storePreview.getY();

        // 3. 주변 반경내 300m 구하기.. (m단위 입력)
        // **선택한 가게에서 가까운 곳**
        //{minX, maxX, minY, maxY}
        double[] aroundXY = locationValue.aroundDist(seletX, selectY, 1500);

        // 가게에서 가장 가까운 최대 두 곳 더 미리보기
        List<StorePreviewDetails> aroundStoreList = new ArrayList<>();
        /*-------
        try{
            aroundStoreList = appStoreDao.getAroundPreview(previewReq.getStoreIdx(),
                    seletX, selectY, aroundXY);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
        */

        // 선택한 가게 까지 포함하기기
        aroundStoreList.add(0, storePreview);
        // 4. 반환할 미리보기 정보 만들기
        List<StorePreviewRes> storePreviewRes = new ArrayList<>();
        for(StorePreviewDetails prevDetail : aroundStoreList){
            // 가게 사진 URL
            if(prevDetail.getStoreLogoUrl() != null && prevDetail.getStoreLogoUrl().length() != 0){
                prevDetail.setStoreLogoUrl(""+s3Client.getUrl(bucketName, prevDetail.getStoreLogoUrl()));
            }

            if(prevDetail.getStoreSignUrl() != null && prevDetail.getStoreSignUrl().length() != 0){
                prevDetail.setStoreSignUrl(""+s3Client.getUrl(bucketName, prevDetail.getStoreSignUrl()));
            }
            // 가게평점
            double star = 0;
            try {
                star = appStoreDao.getStoreStar(prevDetail.getStoreIdx());
            }catch(NullPointerException nullerr){
                star = 0;
            }catch (IncorrectResultSizeDataAccessException error) { // 쿼리문에 해당하는 결과가 없거나 2개 이상일 때
                star = 0;
            }

            // ** 내 위치에서 해당 가게까지의 거리 **
            int distance = (int) locationValue.getDistance(latitude, longitude, prevDetail.getY(), prevDetail.getX());

            // 1Km (1000M) 16분 평군 소요
            // 100M : 1분 36초(96초) ,  10M : 9.6초
            // 거리 / 100M * 96초 / 1분 =
            int duration = locationValue.getDuration(distance); // 분

            // 구독 여부(예외처리 나중에 제대로 할것, EXISTS 써서 null 예외 처리 안해도될수도?)
            int subscribed;
            try{
                subscribed = appStoreDao.getStoreSubscribed(customerIdx, prevDetail.getStoreIdx());
            }catch (IncorrectResultSizeDataAccessException error) { // 쿼리문에 해당하는 결과가 없거나 2개 이상일 때
                subscribed  = 0;
            }catch(Exception e){
                throw new BaseException(DATABASE_ERROR);
            }

            StorePreviewRes prevRes = new StorePreviewRes(
                    prevDetail.getStoreIdx(),
                    prevDetail.getStoreName(),
                    prevDetail.getStoreLogoUrl(),
                    prevDetail.getStoreSignUrl(),
                    star, distance, duration, subscribed
            );

            storePreviewRes.add(prevRes);
        }

        return storePreviewRes;
    }

    public GetAppStoreInfoRes getAppStoreInfo(int userIdx, int storeIdx, Double longitude, Double latitude) throws BaseException {
        // 1. 가게 기본 식별정보 조회
        GetAppStoreInfo storeInfo;
        try {
            storeInfo = appStoreDao.getAppStoreInfo(storeIdx);
        } catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }

        // 2. 가게 별점 가져오기
        double starAvg;
        try {
            starAvg = appStoreDao.getStoreStar(storeIdx);
        }catch (IncorrectResultSizeDataAccessException error) { // 쿼리문에 해당하는 결과가 없거나 2개 이상일 때
            starAvg = 0;
        } catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }

        // 3. 가게 구독 수(찜 수)
        int subscribeCount;
        try {
            subscribeCount = appStoreDao.subscribeCount(storeIdx);
        } catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }

        // 4. 가게 원산지
        String ingredientInfo;
        try {
            ingredientInfo = appStoreDao.getStoreAppDetailIngredientInfo(storeIdx);
        } catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }

        int distance = (int) locationValue.getDistance(latitude, longitude, storeInfo.getY(),  storeInfo.getX());
        int duration = locationValue.getDuration(distance);

        int subCheck;
        try{
            subCheck = subscribeDao.checkSubscribeValid(userIdx, storeIdx);
        }catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);    // 리뷰 조회에 실패하였습니다.
        }

        return new GetAppStoreInfoRes(storeIdx,
                storeInfo.getStoreName(),
                storeInfo.getStoreCategory(),
                storeInfo.getStorePhone(),
                storeInfo.getX(), storeInfo.getY(),
                storeInfo.getStoreAddress(),
                distance, duration,
                starAvg,
                subscribeCount,
                ingredientInfo,
                subCheck);
    }


    public List<StorePreviewRes> getAppStoreList(int userIdx, Double longitude, Double latitude) throws BaseException {
        // 1. 주변 반경 구하기 (1500미터)
        //{minX, maxX, minY, maxY}
        double[] aroundXY = locationValue.aroundDist(longitude, latitude, 1500);

        // 2. 주변 가게 목록 가져오기
        // 가게Idx, 가게이름, 가게주소, 좌표
        // 별점, 구독여부
        List<GetStoreList> storeList;
        try {
            storeList = appStoreDao.getAppStoreList(userIdx, aroundXY);
        } catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);
        }

        // 3. URL, 가게까지 거리 구하기
        List<StorePreviewRes> storePreviewRes = new ArrayList<>();
        for(GetStoreList store : storeList){
            // 가게 사진 URL
            if(store.getStoreLogoUrl() != null && store.getStoreLogoUrl().length() != 0){
                store.setStoreLogoUrl(""+s3Client.getUrl(bucketName, store.getStoreLogoUrl()));
            }
            if(store.getStoreSignUrl() != null && store.getStoreSignUrl().length() != 0){
                store.setStoreSignUrl(""+s3Client.getUrl(bucketName, store.getStoreSignUrl()));
            }

            // ** 내 위치에서 해당 가게까지의 거리 **
            int distance = (int) locationValue.getDistance(latitude, longitude, store.getY(), store.getX());

            // 1Km (1000M) 16분 평군 소요
            // 100M : 1분 36초(96초) ,  10M : 9.6초
            // 거리 / 100M * 96초 / 1분 =
            int duration = locationValue.getDuration(distance); // 분

            storePreviewRes.add(
                    new StorePreviewRes(
                            store.getStoreIdx(),
                            store.getStoreName(),
                            store.getStoreLogoUrl(),
                            store.getStoreSignUrl(),
                            store.getStar(),
                            distance, duration,
                            store.getCustomerIdx() != 0 ? 1:0
                    )
            );
        }

        return storePreviewRes;
    }

    public StorePoint storePoint(int storeIdx) throws BaseException{
        try{
            return appStoreDao.storePoint(storeIdx);
        }catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);
        }

    }
}