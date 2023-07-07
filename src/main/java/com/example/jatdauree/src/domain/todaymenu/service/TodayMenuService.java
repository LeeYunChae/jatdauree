package com.example.jatdauree.src.domain.todaymenu.service;

import com.example.jatdauree.config.BaseException;
import com.example.jatdauree.src.domain.todaymenu.dao.MenuDao;
import com.example.jatdauree.src.domain.todaymenu.dao.TodayMenuDao;
import com.example.jatdauree.src.domain.todaymenu.dto.GetMenusSearchRes;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuListItem;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegReq;
import com.example.jatdauree.src.domain.todaymenu.dto.PostTodayMenuRegRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class TodayMenuService {

    private final MenuDao menuDao;
    private final TodayMenuDao todayMenuDao;


    @Autowired
    public TodayMenuService(MenuDao menuDao, TodayMenuDao todayMenuDao) {
        this.menuDao = menuDao;
        this.todayMenuDao = todayMenuDao;
    }

    //떨이 메뉴 조회
    public ArrayList<GetMenusSearchRes> searchMenu(int sellerIdx) throws BaseException {
        int storeIdx = menuDao.getStroeIdxbySellerIdx(sellerIdx);

        return menuDao.searchMenu(storeIdx);
    }


    public ArrayList<PostTodayMenuRegRes> registerTodayMenu(PostTodayMenuRegReq postTodayMenuRegReq) throws BaseException {

        //현재 시간 구하기
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String date = sdf1.format(now);

        int todayMenuIdx = 0;
        //for문으로 각 행들 열 추가
        for (PostTodayMenuListItem item : postTodayMenuRegReq.getTodayMenuListItems()) {
            todayMenuIdx = todayMenuDao.RegisterTodayMenu(date, postTodayMenuRegReq.getStoreIdx(), item);
        }

        return todayMenuDao.searchTodayMenuTable(todayMenuIdx); // todayMenutable 조회

    }


}

