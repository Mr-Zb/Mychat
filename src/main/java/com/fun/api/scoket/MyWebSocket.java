package com.fun.api.scoket;

import com.alibaba.fastjson.JSON;
import com.fun.api.domain.Mydata;
import com.fun.framework.utils.Constants;
import com.fun.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint(value = "/socket/{token}")
@Component
public class MyWebSocket {
    /**
     * MyWebSocket是多例的 spring是单例的 采用这种方式注入
     */
    private static RedisTemplate redisTemplate;

    @Autowired
    public void setUserService(RedisTemplate redisTemplate) {
        MyWebSocket.redisTemplate = redisTemplate;
    }

    /**
     * 所有的MyWebSocket对象放在一个map中，方便发送
     */
    private static ConcurrentHashMap<String, MyWebSocket> sessionMap = new ConcurrentHashMap<>(50000);
    /**
     * 每个聊天室一个map方便群发信息
     */
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private String userId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        Integer Id = (Integer) redisTemplate.opsForHash().get(Constants.TOKEN, token);
        this.userId = Id + "";
        this.session = session;
        sessionMap.put(Id + "", this);
        //TODO 查询是否有离线消息 有的话发送（ 群消息和个人消息和系统消息）
        // Set range = redisTemplate.opsForZSet().range(Id+"", 0, -1);
//        try {
//            sessionMap.get(this.userId).session.getBasicRemote().sendText("这是服务器返回的信息！");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //range.forEach(e ->{
//        try {
//            if (range.size()>0) {
//                sessionMap.get(this.userId).session.getBasicRemote().sendText(range.toString());
//                //删除所有属于他的消息
//                redisTemplate.opsForZSet().remove(this.userId);
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        // });
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        sessionMap.remove(this.userId);
        System.out.println("有一连接关闭！用户ID为：" + this.userId);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */

//    @OnMessage
//    public void onMessage(String message) {
        //客户端定时ping一下，如果不同就直接重新建立连接
//        /*if (message.equals("ping")){
//            try {
//                this.session.getBasicRemote().sendText("pong");
//            } catch (IOException e) {
//                System.out.println("IO 异常");
//                e.printStackTrace();
//            }
//        }*/
   // }

    /**
     * 发生错误时调用
     *
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        System.out.println("发生错误");
        System.out.println("链接是否打开：" + this.session.isOpen());
        System.out.println("当前session ID：" + this.session.getId());
        error.printStackTrace();
    }

    /**
     * 私聊
     *
     * @param message
     */
    public void sendMessage(String message, String toId, String chat_type, String from_id, String msg) {
        if (sessionMap.containsKey(toId) && sessionMap.get(toId).session.isOpen()) {
            try {
                if (StringUtils.isBlank(msg)) {
                    msg = "ok";
                }
                Mydata mydata = new Mydata();
                mydata.setMsg(msg);
                mydata.setData(message);
                sessionMap.get(toId).session.getBasicRemote().sendText(JSON.toJSONString(mydata));
                redisTemplate.opsForList().rightPush("chatlog_" + toId+ chat_type + "_" + Integer.parseInt(from_id), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //redisTemplate.opsForZSet().add(toId, message, System.currentTimeMillis());
            redisTemplate.opsForList().rightPush("getmessage_" + Integer.parseInt(toId), message);
        }
    }
}