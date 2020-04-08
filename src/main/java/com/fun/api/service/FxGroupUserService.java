package com.fun.api.service;

import com.fun.api.domain.FxGroupInfo;
import com.fun.api.domain.FxGroupUser;
import com.fun.api.mapper.FxGroupUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FxGroupUserService{

    @Autowired
    private FxGroupUserMapper fxGroupUserMapper;



    public int deleteByPrimaryKey(Integer fxId) {
        return fxGroupUserMapper.deleteByPrimaryKey(fxId);
    }
    public int deleteByIds(Integer groupId,Integer userId){
        return fxGroupUserMapper.deleteByIds(groupId,userId);
    }

    public int insert(FxGroupUser record) {
        return fxGroupUserMapper.insert(record);
    }


    public int insertSelective(FxGroupUser record) {
        return fxGroupUserMapper.insertSelective(record);
    }


    public FxGroupUser selectByPrimaryKey(Integer fxId) {
        return fxGroupUserMapper.selectByPrimaryKey(fxId);
    }


    public int updateByPrimaryKeySelective(FxGroupUser record) {
        return fxGroupUserMapper.updateByPrimaryKeySelective(record);
    }


    public int updateByPrimaryKey(FxGroupUser record) {
        return fxGroupUserMapper.updateByPrimaryKey(record);
    }
    public FxGroupUser selectByIds(Integer groupId,Integer userId){
        return fxGroupUserMapper.selectByIds(groupId,userId);
    }
    public FxGroupUser selectExist(Integer groupId,Integer userId){
        return fxGroupUserMapper.selectExist(groupId,userId);
    }

    public List<FxGroupUser> selectByGroupId(Integer groupId){
        return fxGroupUserMapper.selectByGroupId(groupId);
    }

    public List<FxGroupUser> selectByGroupIdAndUserId(Integer groupId,Integer userId){
        return fxGroupUserMapper.selectByGroupIdAndUserId(groupId,userId);
    }
    public List<FxGroupInfo> selectByUserId(Integer userId){
     return fxGroupUserMapper.selectByUserId(userId);
    }
    public int insertForeach(List<FxGroupUser> list){
        return fxGroupUserMapper.insertForeach(list);
    }
}