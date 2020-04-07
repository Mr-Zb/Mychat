package com.fun.api.controller;

import com.fun.api.domain.FxUserInfo;
import com.fun.api.service.FxUserInfoService;
import com.fun.framework.domain.AjaxReturn;
import com.fun.framework.utils.DigestUtils;
import com.fun.framework.utils.PhoneUtil;
import com.fun.framework.utils.RequestUtils;
import com.fun.framework.utils.StringUtils;
import com.fun.framework.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Api(tags="注册")
@RestController
@RequestMapping("/api")
public class RegisterController extends BaseController {

    @Autowired
    private FxUserInfoService userService;
    @Value("${basedir}")
    private String basedir;
    @Value("${uploadWindows}")
    private String uploadWindows;
    /**
     * 用户注册
     * @return
     */
    @ApiOperation(value = "用户注册 【客户端】")
    @RequestMapping(path = {"/reg"} ,method = {RequestMethod.POST})
    public AjaxReturn redister(@ApiParam(name = "account", value = "账号") String account,
                               @ApiParam(name = "userPwd", value = "密码") String userPwd,
                               @ApiParam(name = "nickName", value = "密码") String nickName,
                               HttpServletRequest request
//                                ,
//                               @ApiParam(name = "code", value = "图片验证码") String code,
//                               @ApiParam(name = "tokenKey", value = "图片验证码token")  String tokenKey
    ){
        if (StringUtils.isBlank(account)){
            return new AjaxReturn(501,"手机号不能为空！",null);
        }
        if (!PhoneUtil.isMobileNO(account)){
            return new AjaxReturn(501,"手机号不合法！",null);
        }
        if (userPwd.length()<3){
            return new AjaxReturn(501,"密码应大于三位数！",null);
        }
        if (StringUtils.isBlank(nickName)) {
            return new AjaxReturn(501, "昵称不能为空", null);
        }
//        if (StringUtils.isBlank(code)) {
//            return new AjaxReturn(501, "验证码不能为空", null);
//        }
//        String verification =(String) redisTemplate.opsForHash().get(Constants.KEY_VALIDATION, tokenKey);
//        if (StringUtils.isBlank(verification) || StringUtils.isBlank(code)|| !StringUtils.equalsIgnoreCase(verification, code)) {
//            return new AjaxReturn(501, "验证码不正确", null);
//        }
        FxUserInfo userInfo1 = userService.selectByUserName(account);
        if (userInfo1==null) {
            FxUserInfo userInfo = new FxUserInfo();
            userInfo.setPhoneNo(account);
            userInfo.setPassword(DigestUtils.md5Hex(userPwd));
            userInfo.setCreateIp(RequestUtils.getIp(request));
            userInfo.setNickName(nickName);
            userInfo.setUserState(0);
            userService.insertSelective(userInfo);
        }else {
            return new AjaxReturn(501,"手机号已注册",null);
        }
        return new AjaxReturn(200,"注册成功！",null);
    }
}
