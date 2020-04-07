package com.fun.api.admincontroller;

import com.fun.api.domain.FxAdminInfo;
import com.fun.api.service.FxAdminInfoService;
import com.fun.framework.domain.AjaxReturn;
import com.fun.framework.utils.Constants;
import com.fun.framework.utils.DigestUtils;
import com.fun.framework.utils.StringUtils;
import com.fun.framework.web.controller.BaseController1;
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
@Api(tags="后台登录")
@RestController
@RequestMapping("/api/admin")
public class AdminLoginController extends BaseController1 {

    @Autowired
    private FxAdminInfoService adminInfoService;
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
                              @ApiParam(name = "account", value = "账号")String account,
                              @ApiParam(name = "userPwd", value = "密码")String userPwd,
                              HttpServletRequest request//
    ){
        if (StringUtils.isBlank(account)||StringUtils.isBlank(account)){
            return new AjaxReturn(501, "账号不或密码不能为空!", null);
        }
        // 获取账户信息
        FxAdminInfo fxAdminInfo = adminInfoService.selectByAccount(account);
        //MemUserInfo userInfo = memService.getUserInfoByName(userName);
        if(fxAdminInfo==null){
            return new AjaxReturn(501, "账号不存在!", null);
        }
        if (!fxAdminInfo.getPassword().equals(DigestUtils.md5Hex(userPwd))){
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
                if (fxAdminInfo.getFxId() > 0) {
                    if (StringUtils.length(fxAdminInfo.getPassword()) == 16) {
                        new User(String.valueOf(fxAdminInfo.getFxId()), fxAdminInfo.getPassword(),
                                authorities);
                    }
                    userDetails = new User(String.valueOf(fxAdminInfo.getFxId()),
                            StringUtils.substring(fxAdminInfo.getPassword(), 8, 24), authorities);
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
        Constants.sessions.put(fxAdminInfo.getFxId().longValue(), sessionId);
        log.debug("<<login");
        String tokenStr = StringUtils.systemUuid();
        Map<Object, Object> map = redisTemplate.opsForHash().entries(Constants.ADMINTOKEN);
        map.forEach((k, v) -> {
            if (String.valueOf(fxAdminInfo.getFxId()).equals(v.toString())) {
                redisTemplate.opsForHash().delete(Constants.ADMINTOKEN, k);
            }
        });
        redisTemplate.opsForHash().put(Constants.ADMINTOKEN, tokenStr, fxAdminInfo.getFxId());
        Map map1 = new HashMap();
        map1.put("token",tokenStr);
        return new AjaxReturn(200, null, map1);
    }


    //

    @ApiOperation(value = "管理员登出 【后台】" ,  notes="管理员登出")
    @RequestMapping(path = { "/logout" }, method = { RequestMethod.GET, RequestMethod.POST })
    public AjaxReturn logout(HttpServletRequest request){
        String token = request.getHeader("token");
        log.debug(">>logout");
        request.getSession().invalidate();
        redisTemplate.opsForHash().delete(Constants.ADMINTOKEN,token);
        log.debug("<<logout");
        return new AjaxReturn(200, "登出成功！", null);
    }
}
