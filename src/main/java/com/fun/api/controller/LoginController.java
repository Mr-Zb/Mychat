package com.fun.api.controller;

import com.fun.api.domain.FxUserInfo;
import com.fun.api.service.FxUserInfoService;
import com.fun.framework.domain.AjaxReturn;
import com.fun.framework.utils.Constants;
import com.fun.framework.utils.DigestUtils;
import com.fun.framework.utils.StringUtils;
import com.fun.framework.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Api(tags="登录")
@RestController
@RequestMapping("/api")
public class LoginController extends BaseController {

    @Autowired
    private FxUserInfoService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    private final Map<String, Date> map = Collections.synchronizedMap(new HashMap<>());

    /**
     * 登陆
     * @param account
     * @param userPwd
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户登录 【客户端】" ,  notes="用户登录")
    @RequestMapping(path = {"/login"} ,method = {RequestMethod.POST})
    public AjaxReturn loginPc(//
                              @ApiParam(name = "account", value = "账号") String account,
                              @ApiParam(name = "userPwd", value = "密码")  String userPwd,
                              HttpServletRequest request//
    ){
        // 获取账户信息
        FxUserInfo userInfo = userService.selectByUserName(account);
        //MemUserInfo userInfo = memService.getUserInfoByName(userName);
        if(userInfo==null){
            return new AjaxReturn(501, "账号不存在!", null);
        }
        if (!userInfo.getPassword().equals(DigestUtils.md5Hex(userPwd))){
            return new AjaxReturn(501, "用户名或者密码不正确！", null);
        }
        UserDetails userDetails = null;
        synchronized (this.map) {
            if ((this.map.get(account) != null) && (( this.map.get(account)).after(new Date()))) {
                return new AjaxReturn(501, "短时间不允许重复登陆！", null);
            }
            this.map.put(account, DateTime.now().plusSeconds(3).toDate());
            try {
                Set<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
                if (userInfo.getFxId() > 0) {
                    if (StringUtils.length(userInfo.getPassword()) == 16) {
                        new User(String.valueOf(userInfo.getFxId()), userInfo.getPassword(),
                                authorities);
                    }
                    userDetails = new User(String.valueOf(userInfo.getFxId()),
                            StringUtils.substring(userInfo.getPassword(), 8, 24), authorities);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(userDetails,
                userDetails.getPassword(), userDetails.getAuthorities());
        token.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);
        String sessionId = request.getSession().getId();
        Constants.sessions.put(userInfo.getFxId().longValue(), sessionId);
        log.debug("<<login");
        String tokenStr = StringUtils.systemUuid();
        Map<Object, Object> map = redisTemplate.opsForHash().entries(Constants.TOKEN);
        map.forEach((k, v) -> {
            if (String.valueOf(userInfo.getFxId()).equals(v.toString())) {
                redisTemplate.opsForHash().delete(Constants.TOKEN, k);
            }
        });
        redisTemplate.opsForHash().put(Constants.TOKEN, tokenStr, userInfo.getFxId());
        Map map1 = new HashMap();
        map1.put("token",tokenStr);
        map1.put("userId",userInfo.getFxId());
        map1.put("fxNo",userInfo.getFxNo());
        map1.put("headPortraitBig",userInfo.getHeadPortraitBig());
        map1.put("avatar",userInfo.getAvatar());
        map1.put("nickname",userInfo.getNickName());
        map1.put("phoneNo",userInfo.getPhoneNo());
        map1.put("qrCode",userInfo.getQrCode());
        return new AjaxReturn(200, null, map1);
    }


    //

    @ApiOperation(value = "用户登出 【客户端】" ,  notes="用户登出")
    @RequestMapping(path = { "/logout" }, method = { RequestMethod.POST })
    public AjaxReturn logout(HttpServletRequest request){
        String token = request.getHeader("token");
        log.debug(">>logout");
        request.getSession().invalidate();
        redisTemplate.opsForHash().delete(Constants.TOKEN,token);
        log.debug("<<logout");
        return new AjaxReturn(200, "登出成功！", null);
    }

//    @RequestMapping("/login")
//    public Object login(String name, String password) throws Exception {
//        /**
//         * 这里为了简单，就不验证用户名和密码的正确性了，实际验证跟其他的方式一样，
//         *         就是比对一下输入的用户名密码跟数据的数据是否一样
//         */
//        String token = "";
//        token = JWT.create()
//                .withAudience(name)// 将 user id 保存到 token 里面
//                .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 1000))//定义token的有效期
//                .sign(Algorithm.HMAC256("ConstantKey.PICEA_JWT_KEY"));// 加密秘钥，也可以使用用户保持在数据库中的密码字符串
//        return token;
//    }

}
