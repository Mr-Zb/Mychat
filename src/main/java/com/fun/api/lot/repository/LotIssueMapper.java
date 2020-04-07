package com.fun.api.lot.repository;

import com.fun.api.lot.domaian.LotLotteryIssue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LotIssueMapper {
    Integer insertBatch(@Param("lotLotteryIssues") List<LotLotteryIssue> lotLotteryIssues);

    Integer updateByProductId(@Param("lotLotteryIssue") LotLotteryIssue lotLotteryIssue);
}
