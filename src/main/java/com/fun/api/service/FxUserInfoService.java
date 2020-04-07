package com.fun.api.service;

import com.alibaba.fastjson.JSON;
import com.fun.api.aliyunOSS.AliyunUtil;
import com.fun.api.domain.FxUserInfo;
import com.fun.api.domain.QrCode;
import com.fun.api.mapper.FxUserInfoMapper;
import com.fun.framework.utils.QrCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Date;

@Service
public class FxUserInfoService {

    @Autowired
    private FxUserInfoMapper fxUserInfoMapper;
    @Autowired
    private AliyunUtil aliyunUtil;

    @Value("${userImg}")
    private String userImg;

    public int deleteByPrimaryKey(Integer fxId) {
        return fxUserInfoMapper.deleteByPrimaryKey(fxId);
    }


    public int insert(FxUserInfo record) {
        return fxUserInfoMapper.insert(record);
    }


    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = { Exception.class })
    public int insertSelective(FxUserInfo record) {
        record.setCreateTime(new Date());
        record.setHeadPortraitBig(aliyunUtil.getUserImg());
        record.setAvatar(aliyunUtil.getUserImg());
        fxUserInfoMapper.insertSelective(record);
        QrCode qrCode = new QrCode();
        qrCode.setId(record.getFxId()).setEvent("navigateTo").setType("user");
        String message = JSON.toJSONString(qrCode);
        String tem = System.currentTimeMillis()+".jpg";
        InputStream inputStream = null;
        String aliyun ="";
        try {
            System.err.println(userImg);
            inputStream = QrCodeUtils.encode3(message, aliyunUtil.getUserImg(), true);
            aliyun = aliyunUtil.aliyun(inputStream, tem);
            record.setQrCode(aliyun);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fxUserInfoMapper.updateByPrimaryKeySelective(record);
    }


    public FxUserInfo selectByPrimaryKey(Integer fxId) {
        return fxUserInfoMapper.selectByPrimaryKey(fxId);
    }


    public int updateByPrimaryKeySelective(FxUserInfo record) {
        return fxUserInfoMapper.updateByPrimaryKeySelective(record);
    }

    public FxUserInfo selectByUserName(String phoneNo){
        return fxUserInfoMapper.selectByUserName(phoneNo);
    }
    public int updateByPrimaryKey(FxUserInfo record) {
        return fxUserInfoMapper.updateByPrimaryKey(record);
    }
    public FxUserInfo searchUser(String phoneNo){
        return fxUserInfoMapper.searchUser(phoneNo);
    }
}