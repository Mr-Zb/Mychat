package com.fun.api.service;

import com.fun.api.domain.FxAdminInfo;
import com.fun.api.mapper.FxAdminInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FxAdminInfoService{

    @Autowired
    private FxAdminInfoMapper adminInfoMapper;

    public int deleteByPrimaryKey(Integer pkId) {
        return adminInfoMapper.deleteByPrimaryKey(pkId);
    }


    public int insert(FxAdminInfo record) {
        return adminInfoMapper.insert(record);
    }


    public int insertSelective(FxAdminInfo record) {
        return adminInfoMapper.insertSelective(record);
    }


    public FxAdminInfo selectByPrimaryKey(Integer pkId) {
        return adminInfoMapper.selectByPrimaryKey(pkId);
    }


    public int updateByPrimaryKeySelective(FxAdminInfo record) {
        return adminInfoMapper.updateByPrimaryKeySelective(record);
    }


    public int updateByPrimaryKey(FxAdminInfo record) {
        return adminInfoMapper.updateByPrimaryKey(record);
    }

    public FxAdminInfo selectByAccount(String account){
        return adminInfoMapper.selectByAccount(account);
    }
}
