package com.fun.api.controller;

import com.fun.api.domain.FxFriends;
import com.fun.api.domain.FxRequestState;
import com.fun.api.domain.FxUserInfo;
import com.fun.api.domain.MyList;
import com.fun.api.mapper.FxRequestStateMapper;
import com.fun.api.scoket.MyWebSocket;
import com.fun.api.service.FxFriendsService;
import com.fun.api.service.FxRequestStateService;
import com.fun.api.service.FxUserInfoService;
import com.fun.framework.domain.AjaxReturn;
import com.fun.framework.domain.Pagination;
import com.fun.framework.domain.QueryDto;
import com.fun.framework.utils.StringUtils;
import com.fun.framework.utils.listToSortByName;
import com.fun.framework.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags="用户操作")
@RestController
@RequestMapping("/api")
public class UserController extends BaseController {

    @Autowired
    private FxUserInfoService userInfoService;
    @Autowired
    private FxFriendsService fxFriendsService;
    @Autowired
    private FxRequestStateService fxRequestStateService;

    @Autowired
    private FxRequestStateMapper requestStateMapper;

    @ApiOperation(value = "通讯录 【客户端】" ,  notes="通讯录")
    @RequestMapping(path = { "/friends" }, method = {RequestMethod.POST})
    public AjaxReturn friends(HttpServletRequest request){
        Integer authentication = getAuthentication(request);
        List<FxFriends> fxFriends = fxFriendsService.selectByUserId(authentication);
        List<MyList> myLists = listToSortByName.listToSortByName(fxFriends);
        return new AjaxReturn(200, "查询成功！", myLists);
    }

    @ApiOperation(value = "通讯录 【客户端】" ,  notes="通讯录")
    @RequestMapping(path = { "/myfriends" }, method = {RequestMethod.POST})
    public AjaxReturn myfriends(HttpServletRequest request){
        Integer authentication = getAuthentication(request);
        List<FxFriends> fxFriends = fxFriendsService.selectByUserId(authentication);
        return new AjaxReturn(200, "查询成功！", fxFriends);
    }

    @ApiOperation(value = "我的信息 【客户端】" ,  notes="我的信息")
    @RequestMapping(path = { "/myself" }, method = {RequestMethod.POST })
    public AjaxReturn my(HttpServletRequest request){
        Integer authentication = getAuthentication(request);
        FxUserInfo fxUserInfo = userInfoService.selectByPrimaryKey(authentication);
        return new AjaxReturn(200, "查询成功！", fxUserInfo);
    }
    @ApiOperation(value = "好友信息 【客户端】" ,  notes="好友信息")
    @RequestMapping(path = { "/friend" }, method = {RequestMethod.POST })
    public AjaxReturn friend(HttpServletRequest request,Integer friendId){
        FxUserInfo fxUserInfo = userInfoService.selectByPrimaryKey(friendId);
        FxFriends fxFriends = fxFriendsService.selectFriend(getAuthentication(request), fxUserInfo.getFxId());
        FxFriends fxFriends1 = fxFriendsService.selectFriend( fxUserInfo.getFxId(), getAuthentication(request));
        if (fxFriends==null&&fxFriends1==null){
            fxUserInfo.setFriend(false);
        }else {
            fxUserInfo.setFriend(true);
        }
        return new AjaxReturn(200, "查询成功！", fxUserInfo);
    }

    @ApiOperation(value = "查找好友 【客户端】" ,  notes="查找好友")
    @RequestMapping(path = { "/search/friend" }, method = {RequestMethod.POST })
    public AjaxReturn searchFriend(String keyword){
        FxFriends fxFriends = fxFriendsService.selectByKeyword(keyword);
        return new AjaxReturn(200, "查询成功！", fxFriends);
    }
    @ApiOperation(value = "搜索用户 【客户端】" ,  notes="搜索用户")
    @RequestMapping(path = { "/search" }, method = {RequestMethod.POST })
    public AjaxReturn search(HttpServletRequest request,String phoneNo){

        if (StringUtils.isBlank(phoneNo)){
            return new AjaxReturn(500, "请输入查询用户的手机号！", null);
        }
        FxUserInfo fxUserInfo = userInfoService.searchUser(phoneNo);
        if (fxUserInfo==null){
            return new AjaxReturn(501, "此用户不存在！", null);
        }
        return new AjaxReturn(200, "查询成功！", fxUserInfo);
    }

    @ApiOperation(value = "添加好友 【客户端】" ,  notes="添加好友")
    @RequestMapping(path = { "/request/add" }, method = {RequestMethod.POST })
    public AjaxReturn add(HttpServletRequest request,Integer friendId,String msg){
        Integer authentication = getAuthentication(request);
        if (authentication.equals(friendId)){
            return new AjaxReturn(500, "不能添加自己！", null);
        }
        //再次请求不添加state重新发出通知即可
        FxRequestState fxRequestState1 = fxRequestStateService.selectByIds(authentication, friendId);
        MyWebSocket myWebSocket = new MyWebSocket();
        if (fxRequestState1==null){
            FxRequestState fxRequestState = new FxRequestState();
            fxRequestState.setUserId(friendId);
            fxRequestState.setRequestId(authentication);
            fxRequestState.setRequestState(0);
            fxRequestState.setRequestMsg(msg);
            fxRequestState.setCreateTime(new Date());
            fxRequestStateService.insertSelective(fxRequestState);
            myWebSocket.sendMessage("", friendId+"", "", authentication + "", "updateApplyList");
            return new AjaxReturn(200, "发出请求成功！", null);
        }else {
            FxRequestState fxRequestState = new FxRequestState();
            fxRequestState.setRequestId(authentication);
            fxRequestState.setUserId(friendId);
            if (StringUtils.isNotBlank(fxRequestState1.getRequestMsg())){
                fxRequestState.setRequestMsg(fxRequestState1.getRequestMsg()+"<br />"+msg);
            }else {
                fxRequestState.setRequestMsg(msg);
            }
            fxRequestState.setRequestState(0);
            fxRequestStateService.updateByIds2(fxRequestState);
            myWebSocket.sendMessage("", friendId+"", "", authentication + "", "updateApplyList");
            return new AjaxReturn(200, "发出请求成功！", null);
        }
    }
    @ApiOperation(value = "答应添加好友 【客户端】" ,  notes="答应添加好友")
    @RequestMapping(path = { "/add" }, method = {RequestMethod.POST })
    public AjaxReturn useradd(HttpServletRequest request,Integer state,Integer fxId,String requestMsg){
        fxFriendsService.insertSelective(getAuthentication(request),state,fxId,requestMsg);
        return new AjaxReturn(200, "发出请求成功！", null);
    }

    @ApiOperation(value = "拒绝 【客户端】" ,  notes="拒绝")
    @RequestMapping(path = { "/refuse" }, method = {RequestMethod.POST })
    public AjaxReturn refuse(FxRequestState fxRequestState){
        requestStateMapper.updateByPrimaryKeySelective(fxRequestState);
        return new AjaxReturn(200, "发出请求成功！", null);
    }

    @ApiOperation(value = "查看请求 【客户端】" ,  notes="查看请求")
    @RequestMapping(path = { "/select/request" }, method = {RequestMethod.POST })
    public AjaxReturn selectRequest(HttpServletRequest request, QueryDto queryDto){
        Integer authentication = getAuthentication(request);
        Pagination<FxRequestState> fxRequestStatePagination = fxRequestStateService.selectByUserId(queryDto, authentication);
        List<FxRequestState> fxRequestStates = fxRequestStateService.selectByUserState(authentication);
        int size = fxRequestStates.size();
        fxRequestStatePagination.setCount(new Long(size));
        return new AjaxReturn<>(200,fxRequestStates.size()+"",fxRequestStatePagination);
    }

    @ApiOperation(value = "修改昵称 【客户端】" ,  notes="修改昵称")
    @RequestMapping(path = { "/update/nick" }, method = {RequestMethod.POST })
    public AjaxReturn updateNikeName(HttpServletRequest request, String nickName,String avatar){
        if (StringUtils.isBlank(nickName)){
            return new AjaxReturn<>(500,"昵称不能为空！",null);
        }
        FxUserInfo fxUserInfo = new FxUserInfo();
        fxUserInfo.setFxId(getAuthentication(request));
        fxUserInfo.setAvatar(avatar);
        fxUserInfo.setNickName(nickName);
        userInfoService.updateByPrimaryKeySelective(fxUserInfo);
        return new AjaxReturn<>(200,null,null);
    }

    @ApiOperation(value = "修改头像 【客户端】" ,  notes="修改头像")
    @RequestMapping(path = { "/update/avatar" }, method = {RequestMethod.POST })
    public AjaxReturn updateImg(HttpServletRequest request,String avatar){
        FxUserInfo fxUserInfo = new FxUserInfo();
        fxUserInfo.setFxId(getAuthentication(request));
        fxUserInfo.setAvatar(avatar);
        userInfoService.updateByPrimaryKeySelective(fxUserInfo);
        return new AjaxReturn<>(200,null,null);
    }

    @ApiOperation(value = "删除好友 【客户端】" ,  notes="删除好友")
    @RequestMapping(path = { "/delete" }, method = {RequestMethod.POST })
    public AjaxReturn delete(HttpServletRequest request,Integer friendId){
        FxFriends fxFriends = new FxFriends();
        fxFriends.setUserId(getAuthentication(request));
        fxFriends.setFriendId(friendId);
        fxFriendsService.deleteByIds(fxFriends);
        return new AjaxReturn<>(200,null,null);
    }

//    public static String[] sort(String [] data){
//        if(data==null || data.length==0){
//            return null;
//        }
//        Comparator<Object> comparator = Collator.getInstance(java.util.Locale.CHINA);
//        Arrays.sort(data, comparator);
//        return data;
//    }
}
