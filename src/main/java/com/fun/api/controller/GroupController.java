package com.fun.api.controller;

import com.alibaba.fastjson.JSON;
import com.fun.api.aliyunOSS.AliyunUtil;
import com.fun.api.domain.FxGroupInfo;
import com.fun.api.domain.FxGroupUser;
import com.fun.api.domain.FxUserInfo;
import com.fun.api.domain.ImMessage;
import com.fun.api.scoket.MyWebSocket;
import com.fun.api.service.FxGroupInfoService;
import com.fun.api.service.FxGroupUserService;
import com.fun.api.service.FxUserInfoService;
import com.fun.framework.domain.AjaxReturn;
import com.fun.framework.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;


@Slf4j
@Api(tags="群管理消息")
@RestController
@RequestMapping("/api")
public class GroupController extends BaseController {

    @Autowired
    private FxUserInfoService userInfoService;
    @Autowired
    private FxGroupInfoService fxGroupInfoService;
    @Autowired
    private FxGroupUserService fxGroupUserService;
    @Value("${basedir}")
    private String basedir;
    @Value("${uploadWindows}")
    private String uploadWindows;
    @Autowired
    private AliyunUtil aliyunUtil;

    @ApiOperation(value = "创建群聊 【客户端】" ,  notes="创建群聊")
    @RequestMapping(path = { "/create" }, method = {RequestMethod.POST })
    public AjaxReturn recall(HttpServletRequest request, @NotNull Integer[] ids,@NotNull String groupName){
        if (ids.length<2){
            return new AjaxReturn<>(500,"请选择两个及以上好友！",null);
        }
        Integer fromId = getAuthentication(request);
        FxGroupInfo fxGroupInfo = new FxGroupInfo();
        fxGroupInfo.setGroupName(groupName);
        fxGroupInfo.setGroupNotice("");
        fxGroupInfo.setGroupAvatar(aliyunUtil.getGroupImg());
        int i = fxGroupInfoService.insertSelective(fxGroupInfo, ids, fromId,aliyunUtil.getGroupImg());
        FxUserInfo fxUserInfo = userInfoService.selectByPrimaryKey(fromId);
        if (i>0){
            // 消息推送
            ImMessage imMessage = new ImMessage();
            imMessage.setId(System.currentTimeMillis()+"");
            imMessage.setFrom_avatar(fxUserInfo.getAvatar());
            imMessage.setFrom_name(fxUserInfo.getNickName());
            imMessage.setFrom_id(fromId);
            imMessage.setTo_id(fxGroupInfo.getFxId());
            imMessage.setType("system");
            imMessage.setTo_name(fxGroupInfo.getGroupName());
            imMessage.setTo_avatar(aliyunUtil.getGroupImg());
            imMessage.setChat_type("group");
            imMessage.setData("创建群聊成功，可以开始聊天啦");
            imMessage.setOptions(null);
            imMessage.setCreate_time(System.currentTimeMillis());
            imMessage.setIs_remove(0);
            MyWebSocket myWebSocket = new MyWebSocket();
            String message = JSON.toJSONString(imMessage);
            myWebSocket.sendMessage(message,fromId+"","group",fromId+"",null);
            for (Integer id : ids){
                myWebSocket.sendMessage(message,id+"","group",fromId+"",null);
            }
            return new AjaxReturn<>(200,"创建成功！",null);
        }
        return new AjaxReturn<>(500,"创建失败！",null);
    }
    @ApiOperation(value = "群成员信息 【客户端】" ,  notes="群成员信息")
    @RequestMapping(path = { "/users" }, method = {RequestMethod.POST })
    public AjaxReturn users(HttpServletRequest request, @NotNull Integer groupId){
        Integer authentication = getAuthentication(request);
        FxGroupInfo fxGroupInfo = fxGroupInfoService.selectByPrimaryKey(groupId);
        List<FxGroupUser> fxGroupUsers = fxGroupUserService.selectByGroupIdAndUserId(groupId, authentication);
        Map<String, Object> map = new HashMap<>();
        map.put("users",fxGroupUsers);
        map.put("groupInfo",fxGroupInfo);
        return new AjaxReturn<>(200,"查询成功！",map);
    }

    @ApiOperation(value = "用户群列表 【客户端】" ,  notes="用户群列表")
    @RequestMapping(path = { "/groups" }, method = {RequestMethod.POST })
    public AjaxReturn groups(HttpServletRequest request){
        Integer authentication = getAuthentication(request);
        List<FxGroupInfo> fxGroupInfos = fxGroupUserService.selectByUserId(authentication);
        return new AjaxReturn<>(200,"查询成功！",fxGroupInfos);
    }

    @ApiOperation(value = "退群/解散群 【客户端】" ,  notes="退群/解散群")
    @RequestMapping(path = { "/quit" }, method = {RequestMethod.POST })
    public AjaxReturn quit(HttpServletRequest request,  @NotNull Integer groupId){
        Integer authentication = getAuthentication(request);
        FxGroupInfo fxGroupInfo = fxGroupInfoService.selectByPrimaryKey(groupId);
        if (fxGroupInfo==null){
            return new AjaxReturn<>(500,"该群聊不存在！",null);
        }
        FxGroupUser fxGroupUser = fxGroupUserService.selectExist(groupId, authentication);
        if (fxGroupInfo==null){
            return new AjaxReturn<>(500,"你不是该群成员！",null);
        }
        FxUserInfo fxUserInfo = userInfoService.selectByPrimaryKey(authentication);
        // 消息推送
        ImMessage imMessage = new ImMessage();
        //如果是群主则解散群
        if (fxGroupUser.getUserIdentity()==0){
            fxGroupInfoService.deleteByPrimaryKey(groupId);
            imMessage.setData("该群已被解散");
        }else {
            fxGroupUserService.deleteByIds(groupId,authentication);
            imMessage.setData(fxUserInfo.getNickName()+" 退出该群聊");
            imMessage.setId(System.currentTimeMillis()+"");
            imMessage.setFrom_avatar(fxUserInfo.getAvatar());
            imMessage.setFrom_name(fxUserInfo.getNickName());
            imMessage.setFrom_id(authentication);
            imMessage.setTo_id(groupId);
            imMessage.setType("system");
            imMessage.setTo_name(fxGroupInfo.getGroupName());
            imMessage.setTo_avatar(fxGroupInfo.getGroupAvatar());
            imMessage.setChat_type("group");
            imMessage.setOptions(null);
            imMessage.setCreate_time(System.currentTimeMillis());
            imMessage.setIs_remove(0);
            List<FxGroupUser> fxGroupUsers = fxGroupUserService.selectByGroupId(groupId);
            MyWebSocket myWebSocket = new MyWebSocket();
            String message = JSON.toJSONString(imMessage);
            fxGroupUsers.forEach(fx->{
                myWebSocket.sendMessage(message,fx.getUserId()+"","group",authentication+"",null);
            });
        }
        return new AjaxReturn<>(200,"操作成功！",null);
    }
    //
    @ApiOperation(value = "修改群名称 【客户端】" ,  notes="修改群名称")
    @RequestMapping(path = { "/rename" }, method = {RequestMethod.POST })
    public AjaxReturn rename(HttpServletRequest request, @NotNull Integer groupId, @NotNull String name){
        Integer authentication = getAuthentication(request);
        FxGroupUser fxGroupUser = fxGroupUserService.selectByIds(groupId, authentication);
        if (fxGroupUser==null){
            return new AjaxReturn<>(500,"该群聊不存在！",null);
        }
        if (fxGroupUser.getUserIdentity()>1){
            return new AjaxReturn<>(500,"你不是管理员，没有权限！",null);
        }
        FxUserInfo fxUserInfo = userInfoService.selectByPrimaryKey(authentication);
        FxGroupInfo fxGroupInfo = fxGroupInfoService.selectByPrimaryKey(groupId);
        fxGroupInfo.setGroupName(name);
        fxGroupInfoService.updateByPrimaryKeySelective(fxGroupInfo);
        ImMessage imMessage = new ImMessage();
        imMessage.setId(System.currentTimeMillis()+"");
        imMessage.setFrom_avatar(fxUserInfo.getAvatar());
        imMessage.setFrom_name(fxUserInfo.getNickName());
        imMessage.setFrom_id(authentication);
        imMessage.setTo_id(groupId);
        imMessage.setType("system");
        imMessage.setData(fxUserInfo.getNickName()+ "修改群名称为:"+ name);
        imMessage.setTo_name(name);
        imMessage.setTo_avatar(fxGroupInfo.getGroupAvatar());
        imMessage.setChat_type("group");
        imMessage.setOptions(null);
        imMessage.setCreate_time(System.currentTimeMillis());
        imMessage.setIs_remove(0);
        List<FxGroupUser> fxGroupUsers = fxGroupUserService.selectByGroupId(groupId);
        MyWebSocket myWebSocket = new MyWebSocket();
        String message = JSON.toJSONString(imMessage);
       // myWebSocket.sendMessage(message,authentication+"","group",authentication+"",null);
        fxGroupUsers.forEach(fx->{
            myWebSocket.sendMessage(message,fx.getUserId()+"","group",authentication+"",null);
        });
        return new AjaxReturn<>(200,"操作成功",null);
    }
    //
    @ApiOperation(value = "推送群公告 【客户端】" ,  notes="推送群公告")
    @RequestMapping(path = { "/remark" }, method = {RequestMethod.POST })
    public AjaxReturn remark(HttpServletRequest request, @NotNull Integer groupId, @NotNull String remark){
        Integer authentication = getAuthentication(request);
        FxGroupUser fxGroupUser = fxGroupUserService.selectByIds(groupId, authentication);
        if (fxGroupUser==null){
            return new AjaxReturn<>(500,"该群聊不存在或者已被封禁！",null);
        }
        if (fxGroupUser.getUserIdentity()>1){
            return new AjaxReturn<>(500,"你不是管理员，没有权限！",null);
        }
        FxUserInfo fxUserInfo = userInfoService.selectByPrimaryKey(authentication);
        FxGroupInfo fxGroupInfo = fxGroupInfoService.selectByPrimaryKey(groupId);
        fxGroupInfo.setGroupNotice(remark);
        fxGroupInfoService.updateByPrimaryKeyWithBLOBs(fxGroupInfo);
        ImMessage imMessage = new ImMessage();
        imMessage.setId(System.currentTimeMillis()+"");
        imMessage.setFrom_avatar(fxUserInfo.getAvatar());
        imMessage.setFrom_name(fxUserInfo.getNickName());
        imMessage.setFrom_id(authentication);
        imMessage.setTo_id(groupId);
        imMessage.setType("system");
        imMessage.setData("[新公告] "+remark);
        imMessage.setTo_name(fxGroupInfo.getGroupName());
        imMessage.setTo_avatar(fxGroupInfo.getGroupAvatar());
        imMessage.setChat_type("group");
        imMessage.setOptions(null);
        imMessage.setCreate_time(System.currentTimeMillis());
        imMessage.setIs_remove(0);
        List<FxGroupUser> fxGroupUsers = fxGroupUserService.selectByGroupId(groupId);
        MyWebSocket myWebSocket = new MyWebSocket();
        String message = JSON.toJSONString(imMessage);
       // myWebSocket.sendMessage(message,authentication+"","group",authentication+"",null);
        fxGroupUsers.forEach(fx->{
            myWebSocket.sendMessage(message,fx.getUserId()+"","group",authentication+"",null);
        });
        return new AjaxReturn<>(200,"操作成功",null);
    }


    @ApiOperation(value = "踢出某个群成员 【客户端】" ,  notes="踢出某个群成员")
    @RequestMapping(path = { "/kickoff" }, method = {RequestMethod.POST })
    public AjaxReturn kickoff(HttpServletRequest request, @NotNull Integer groupId, @NotNull Integer userId ){
        Integer authentication = getAuthentication(request);
        if (userId == authentication) {
            return new AjaxReturn<>(500,"'不能踢自己'！",null);
        }
        FxGroupUser fxGroupUser = fxGroupUserService.selectByIds(groupId, authentication);
        if (fxGroupUser==null){
            return new AjaxReturn<>(500,"该群聊不存在或被封禁！",null);
        }
        if (fxGroupUser.getUserIdentity()>1){
            return new AjaxReturn<>(500,"你不是管理员，没有权限！",null);
        }
        FxGroupUser fxGroupUser2 = fxGroupUserService.selectByIds(groupId, authentication);
        if (fxGroupUser2==null){
            return new AjaxReturn<>(500,"对方不是该群成员！",null);
        }
        fxGroupUserService.deleteByIds(groupId,userId);
        FxUserInfo fxUserInfo = userInfoService.selectByPrimaryKey(authentication);
        FxUserInfo user = userInfoService.selectByPrimaryKey(userId);
        FxGroupInfo fxGroupInfo = fxGroupInfoService.selectByPrimaryKey(groupId);
        ImMessage imMessage = new ImMessage();
        imMessage.setId(System.currentTimeMillis()+"");
        imMessage.setFrom_avatar(fxUserInfo.getAvatar());
        imMessage.setFrom_name(fxUserInfo.getNickName());
        imMessage.setFrom_id(authentication);
        imMessage.setTo_id(groupId);
        imMessage.setType("system");
        imMessage.setData(fxUserInfo.getNickName()+"将 "+user.getNickName()+"移出群聊");
        imMessage.setTo_name(fxGroupInfo.getGroupName());
        imMessage.setTo_avatar(fxGroupInfo.getGroupAvatar());
        imMessage.setChat_type("group");
        imMessage.setOptions(null);
        imMessage.setCreate_time(System.currentTimeMillis());
        imMessage.setIs_remove(0);
        List<FxGroupUser> fxGroupUsers = fxGroupUserService.selectByGroupId(groupId);
        MyWebSocket myWebSocket = new MyWebSocket();
        String message = JSON.toJSONString(imMessage);
        fxGroupUsers.forEach(fx->{
            myWebSocket.sendMessage(message,fx.getUserId()+"","group",authentication+"",null);
        });
        return new AjaxReturn<>(200,"操作成功",null);
    }

    ///group/invite

    @ApiOperation(value = "邀请好友加入群 【客户端】" ,  notes="邀请好友加入群")
    @RequestMapping(path = { "/invite" }, method = {RequestMethod.POST })
    public AjaxReturn invite(HttpServletRequest request, @NotNull Integer groupId, @NotNull Integer[] ids ){
        Integer fromId = getAuthentication(request);
        FxGroupInfo fxGroupInfo = new FxGroupInfo();
        FxGroupInfo fxGroupInfo1 = fxGroupInfoService.selectByPrimaryKey(groupId);
        if (fxGroupInfo1==null){
            return new AjaxReturn<>(500,"该群聊被解散了！",null);
        }
        if (fxGroupInfo1.getGroupState()==1){
            return new AjaxReturn<>(500,"该群聊被封禁！",null);
        }
        List<FxGroupUser> list = new ArrayList<>();
        for (Integer id : ids) {
            FxGroupUser fxGroupUser = new FxGroupUser();
            fxGroupUser.setGroupId(groupId);
            fxGroupUser.setUserId(id);
            fxGroupUser.setUserIdentity(2);
            fxGroupUser.setCreateTime(new Date());
            fxGroupUser.setUpdateTime(new Date());
            fxGroupUser.setUserGroupState(0);
            list.add(fxGroupUser);
        }
         fxGroupUserService.insertForeach(list);
        FxUserInfo fxUserInfo = userInfoService.selectByPrimaryKey(fromId);
        String str="";
        for(Integer id:ids){
            FxUserInfo fxUserInfo1= userInfoService.selectByPrimaryKey(id);
            str=fxUserInfo1.getNickName()+"...";
        }
        ImMessage imMessage = new ImMessage();
        imMessage.setId(System.currentTimeMillis()+"");
        imMessage.setFrom_avatar(fxUserInfo.getAvatar());
        imMessage.setFrom_name(fxUserInfo.getNickName());
        imMessage.setFrom_id(fromId);
        imMessage.setTo_id(groupId);
        imMessage.setType("system");
        imMessage.setData(fxUserInfo.getNickName() +"邀请" +str+"加入群聊");
        imMessage.setTo_name(fxGroupInfo.getGroupName());
        imMessage.setTo_avatar(fxGroupInfo.getGroupAvatar());
        imMessage.setChat_type("group");
        imMessage.setOptions(null);
        imMessage.setCreate_time(System.currentTimeMillis());
        imMessage.setIs_remove(0);
        List<FxGroupUser> fxGroupUsers = fxGroupUserService.selectByGroupId(groupId);
        MyWebSocket myWebSocket = new MyWebSocket();
        String message = JSON.toJSONString(imMessage);
        fxGroupUsers.forEach(fx->{
            myWebSocket.sendMessage(message,fx.getUserId()+"","group",fromId+"",null);
        });
        return new AjaxReturn<>(200,"操作成功",null);
    }
}
