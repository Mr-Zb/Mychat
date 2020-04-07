package com.fun.framework.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.fun.framework.support.BusinessException;
import com.fun.framework.support.TokenException;
import com.fun.framework.utils.Constants;
import com.fun.framework.utils.DateFormatUtils;
import com.fun.framework.utils.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.fun.framework.utils.NumberUtils;
import com.fun.framework.web.support.binder.DateEditor;
import com.fun.framework.web.support.binder.DoubleEditor;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
public abstract class BaseController {

    @Resource
    private RedisTemplate redisTemplate;

    private Map<Integer, String> sync1 = new HashMap<Integer, String>();
    private Map<String, String> sync2 = new HashMap<String, String>();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public Integer getAuthentication(HttpServletRequest request) throws TokenException {
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)){
            throw new TokenException(Integer.valueOf(201),"当前不在线，请登录！");
        }
        Integer userId = (Integer) redisTemplate.opsForHash().get(Constants.TOKEN,token);
        if (userId==null){
            throw new TokenException(Integer.valueOf(202),"用户在其他地方已经登录，请重新登录！");
        }
        redisTemplate.opsForHash().put(Constants.ONLINE_STATUS,userId,new Date());
        return userId;
    }
    /**
     * 时间类型绑定
     *
     * @param binder
     * @throws Exception
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Float.class, new DoubleEditor());
        binder.registerCustomEditor(Double.class, new DoubleEditor());
    }



    protected String getlock(Integer userId) {
        if (!this.sync1.containsKey(userId)) {
            this.sync1.put(userId, StringUtils.systemUuid());
        }
        return this.sync1.get(userId);
    }

    protected String getlock(String userId) {
        if (!this.sync2.containsKey(userId)) {
            this.sync2.put(userId, StringUtils.systemUuid());
        }
        return this.sync2.get(userId);
    }

    @ExceptionHandler({ Throwable.class })
    @ResponseBody
    @ResponseStatus(code = HttpStatus.OK)
    public ModelAndView runtimeExceptionHandler(Throwable throwable) {
        FastJsonJsonView jsonView = new FastJsonJsonView();
        if ((throwable instanceof BusinessException)) {
            jsonView.addStaticAttribute("code", ((BusinessException) throwable).getCode());
            //jsonView.addStaticAttribute("info", ((BusinessException) throwable).getInfo());
            jsonView.addStaticAttribute("info", "亲，服务器开小差了，请重试！");
        } if ((throwable instanceof TokenException)) {
            jsonView.addStaticAttribute("code", ((TokenException) throwable).getCode());
            jsonView.addStaticAttribute("info", "登录超时，请重新登陆！");
        }else {
            jsonView.addStaticAttribute("code", Integer.valueOf(501));
            if (throwable.getMessage() != null){
                jsonView.addStaticAttribute("info", "亲，服务器开小差了，请重试！");
            }
        }
        if (this.logger.isErrorEnabled()) {
            this.logger.error("亲，服务器开小差了，请重试！", throwable);
        }
        return new ModelAndView(jsonView);
    }
}
