package com.fun.api.mapper;

import com.fun.api.domain.FxRequestState;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FxRequestStateMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fx_request_state
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer fxId);
    int deleteByIds(@Param("userId") Integer userId, @Param("requestId") Integer requestId);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fx_request_state
     *
     * @mbggenerated
     */
    int insert(FxRequestState record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fx_request_state
     *
     * @mbggenerated
     */
    int insertSelective(FxRequestState record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fx_request_state
     *
     * @mbggenerated
     */
    FxRequestState selectByPrimaryKey(Integer fxId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fx_request_state
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(FxRequestState record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fx_request_state
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(FxRequestState record);
    int updateByIds(FxRequestState record);
    Page<FxRequestState> selectByUserId(@Param("userId")Integer userId);
    FxRequestState selectByIds(@Param("requestId")Integer requestId,@Param("userId")Integer userId);
    int updateByIds2(FxRequestState record);

    List<FxRequestState> selectByUserState(@Param("userId")Integer userId);
}