package com.fun.api.service;

import com.fun.api.domain.FxRequestState;
import com.fun.api.mapper.FxRequestStateMapper;
import com.fun.framework.domain.Pagination;
import com.fun.framework.domain.QueryDto;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FxRequestStateService {
    @Autowired
    private FxRequestStateMapper fxRequestStateMapper;

    public int deleteByPrimaryKey(Integer fxId) {
        return fxRequestStateMapper.deleteByPrimaryKey(fxId);
    }
    public int deleteByIds(Integer userId,Integer requestId){
        return fxRequestStateMapper.deleteByIds(userId,requestId);
    }

    public int insert(FxRequestState record) {
        return fxRequestStateMapper.insert(record);
    }


    public int insertSelective(FxRequestState record) {
        return fxRequestStateMapper.insertSelective(record);
    }


    public FxRequestState selectByPrimaryKey(Integer fxId) {
        return fxRequestStateMapper.selectByPrimaryKey(fxId);
    }


    public int updateByPrimaryKeySelective(FxRequestState record) {
        return fxRequestStateMapper.updateByPrimaryKeySelective(record);
    }


    public int updateByPrimaryKey(FxRequestState record) {
        return fxRequestStateMapper.updateByPrimaryKey(record);
    }
    public int updateByIds(FxRequestState record){
        return fxRequestStateMapper.updateByIds(record);
    }
    public int updateByIds2(FxRequestState record){
        return fxRequestStateMapper.updateByIds2(record);
    }
    public Pagination<FxRequestState> selectByUserId(QueryDto queryDto,Integer userId){
        PageHelper.startPage(queryDto.getPage().intValue(), queryDto.getRows().intValue(), true);
        Page<FxRequestState> page = fxRequestStateMapper.selectByUserId(userId);
        return new Pagination(page.getTotal(), page.getResult());
    }

    public FxRequestState selectByIds(Integer requestId,Integer userId){
        return fxRequestStateMapper.selectByIds(requestId,userId);
    }

    public List<FxRequestState> selectByUserState(Integer userId){
        return fxRequestStateMapper.selectByUserState(userId);
    }
}
