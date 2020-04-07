package com.fun.api.lot.service;

import com.fun.api.lot.domaian.LotLotteryIssue;
import com.fun.api.lot.repository.LotIssueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LotIssueService {
    @Autowired
    private LotIssueMapper lotIssueMapper;

    public Integer insertBatch(List<LotLotteryIssue> lotLotteryIssues) {
        return this.lotIssueMapper.insertBatch(lotLotteryIssues);
    }

    public Integer updateByProductId(LotLotteryIssue lotLotteryIssue) {
        return this.lotIssueMapper.updateByProductId(lotLotteryIssue);
    }
}
