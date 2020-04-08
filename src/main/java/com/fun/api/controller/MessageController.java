package com.fun.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fun.api.domain.*;
import com.fun.api.scoket.MyWebSocket;
import com.fun.api.service.FxFriendsService;
import com.fun.api.service.FxGroupInfoService;
import com.fun.api.service.FxGroupUserService;
import com.fun.api.service.FxUserInfoService;
import com.fun.framework.domain.AjaxReturn;
import com.fun.framework.utils.StringUtils;
import com.fun.framework.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;


@Slf4j
@Api(tags = "发送消息")
@RestController
@RequestMapping("/api")
public class MessageController extends BaseController {

    @Autowired
    private FxUserInfoService userInfoService;
    @Autowired
    private FxFriendsService fxFriendsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FxGroupUserService fxGroupUserService;

    @Autowired
    private FxGroupInfoService fxGroupInfoService;

    @ApiOperation(value = "发送消息 【客户端】", notes = "删除好友")
    @RequestMapping(path = {"/send"}, method = {RequestMethod.POST})
    public AjaxReturn send(HttpServletRequest request, @NotNull String to_id, @NotNull String chat_type, @NotNull String type, @NotNull String data, String options) {
        Integer fromId = getAuthentication(request);
        FxUserInfo fxUserInfo = userInfoService.selectByPrimaryKey(fromId);
        if (fxUserInfo.getUserState() == 1) {
            return new AjaxReturn<>(500, "此账号已被禁用", null);
        }
        //如果单聊
        MyWebSocket myWebSocket = new MyWebSocket();
        FxFriends fxFriends = null;
        if ("user".equals(chat_type)) {
            fxFriends = fxFriendsService.selectFriend(fromId, Integer.parseInt(to_id));
            if (fxFriends == null || "1".equals(fxFriends.getIsBlack())) {
                return new AjaxReturn<>(500, "对方不存在或者已经把你拉黑", null);
            }
            if (fxFriends.getUserState() == 1) {
                return new AjaxReturn<>(500, "对方账号已被禁用", null);
            }
            ImMessage imMessage = new ImMessage();
            imMessage.setId(System.currentTimeMillis() + "");
            imMessage.setFrom_avatar(fxUserInfo.getAvatar());
            imMessage.setFrom_name(fxUserInfo.getNickName());
            imMessage.setFrom_id(fromId);
            imMessage.setTo_id(Integer.parseInt(to_id));
            imMessage.setType(type);
            //有备注显示备注没备注显示昵称
            imMessage.setTo_name(StringUtils.isBlank(fxFriends.getFriendRemark()) ? fxFriends.getNickName() : fxFriends.getFriendRemark());
            imMessage.setTo_avatar(fxFriends.getAvatar());
            imMessage.setChat_type(chat_type);
            imMessage.setData(data);
            imMessage.setOptions(null);
            imMessage.setCreate_time(System.currentTimeMillis());
            imMessage.setIs_remove(0);
            if ("video".equals(imMessage.getType())) {
                imMessage.setOptions(new options().setPoster(data+"?x-oss-process=video/snapshot,t_10,m_fast,w_300,f_png"));
            }
            // 音频，带上音频时长
            if ("audio".equals(imMessage.getType())) {
                JSONObject jsonObject = JSON.parseObject(options);
                String action = jsonObject.getString("time");
                imMessage.setOptions(new options().setTime(action));
            }

            String message = JSON.toJSONString(imMessage);
            myWebSocket.sendMessage(message, to_id, chat_type, fromId + "", null);
            //保存聊天信息
            //redisTemplate.opsForList().rightPush("chatlog_" + fromId + chat_type + "_" + Integer.parseInt(to_id), message);
            return new AjaxReturn<>(200, null, imMessage);
        } else {
            //TODO
            //查看是否在群中 是否被禁言
            FxGroupUser fxGroupUser = fxGroupUserService.selectByIds(Integer.parseInt(to_id), fromId);
            if (fxGroupUser == null) {
                return new AjaxReturn<>(500, "该群聊不存在或你被禁言了！", null);
            }
            List<FxGroupUser> fxGroupUsers = fxGroupUserService.selectByGroupId(Integer.parseInt(to_id));
            FxGroupInfo fxGroupInfo = fxGroupInfoService.selectByPrimaryKey(Integer.parseInt(to_id));

            //群聊
            ImMessage imMessage = new ImMessage();
            imMessage.setId(System.currentTimeMillis() + "");
            imMessage.setFrom_avatar(fxUserInfo.getAvatar());
            imMessage.setFrom_name(fxUserInfo.getNickName());
            imMessage.setFrom_id(fromId);
            imMessage.setTo_id(Integer.parseInt(to_id));
            imMessage.setType(type);
            //有备注显示备注没备注显示昵称
            imMessage.setTo_name(fxGroupInfo.getGroupName());
            imMessage.setTo_avatar(fxGroupInfo.getGroupAvatar());
            imMessage.setChat_type(chat_type);
            imMessage.setData(data);
            imMessage.setOptions(null);
            imMessage.setCreate_time(System.currentTimeMillis());
            imMessage.setIs_remove(0);
            if ("video".equals(imMessage.getType())) {
                imMessage.setOptions(new options().setPoster(data+"?x-oss-process=video/snapshot,t_10,m_fast,w_300,f_png"));
            }
            // 音频，带上音频时长
            if ("audio".equals(imMessage.getType())) {
                JSONObject jsonObject = JSON.parseObject(options);
                String action = jsonObject.getString("time");
                imMessage.setOptions(new options().setTime(action));
            }
            fxGroupUsers.forEach(user -> {
                String message = JSON.toJSONString(imMessage);
                if (!user.getUserId().equals(fromId)){
                    myWebSocket.sendMessage(message, user.getUserId() + "", chat_type, fromId + "", null);
                }
            });
            return new AjaxReturn<>(200, null, imMessage);
        }
    }

    @ApiOperation(value = "消息消息 【客户端】", notes = "消息消息")
    @RequestMapping(path = {"/getmessage"}, method = {RequestMethod.POST})
    public AjaxReturn getmessage(HttpServletRequest request) {
        Integer fromId = getAuthentication(request);
        String key = "getmessage_" + fromId;
        List list = redisTemplate.opsForList().range(key, 0, -1);
        //redisTemplate.opsForList().trim(key, 1, 0);
        list.forEach(message -> {
            MyWebSocket myWebSocket = new MyWebSocket();
            JSONObject jsonObject = JSON.parseObject((String) message);
            String from_id = (String) jsonObject.get("from_id");
            String chat_type = (String) jsonObject.get("chat_type");
            myWebSocket.sendMessage((String) message, fromId + "",chat_type , from_id, null);
        });
        return new AjaxReturn<>(200, "发送成功！", null);
    }

    @ApiOperation(value = "撤回 【客户端】", notes = "撤回")
    @RequestMapping(path = {"/recall"}, method = {RequestMethod.POST})
    public AjaxReturn recall(HttpServletRequest request, @NotNull String to_id, @NotNull String chat_type, @NotNull String id) {
        Integer fromId = getAuthentication(request);
        ImMessage imMessage = new ImMessage();
        imMessage.setId(id);
        imMessage.setFrom_id(fromId);
        imMessage.setChat_type(chat_type);
        imMessage.setTo_id(Integer.parseInt(to_id));
        //// 单聊
        MyWebSocket myWebSocket = new MyWebSocket();
        if ("user".equals(chat_type)) {
            String message = JSON.toJSONString(imMessage);
            myWebSocket.sendMessage(message, to_id, chat_type, fromId + "", "recall");
            return new AjaxReturn<>(200, null, imMessage);
        } else {
            List<FxGroupUser> fxGroupUsers = fxGroupUserService.selectByGroupId(Integer.parseInt(to_id));
            String message = JSON.toJSONString(imMessage);
            fxGroupUsers.forEach(fx->{
             myWebSocket.sendMessage(message, to_id, chat_type, fromId + "", "recall");
            });
            return new AjaxReturn<>(200, null, imMessage);
        }
    }

}
