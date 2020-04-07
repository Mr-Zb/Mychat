package com.fun.framework.web.controller;

import com.fun.framework.domain.AjaxReturn;
import com.fun.framework.utils.Constants;
import com.fun.framework.utils.StringUtils;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Api(tags="图片二验证码")

@RestController
@RequestMapping("/api")
//@Api(value = "validation", consumes = "application/json", produces = "application/json", protocols = "http")
public class ValidationController {
	private @Autowired DefaultKaptcha captchaProducer;
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 输出验证码
	 * @return
	 * @throws IOException
	 */

	@ApiOperation(value = "图片二验证码】")
	@RequestMapping(path = { "/validation" }, method = { RequestMethod.GET, RequestMethod.POST })
	public AjaxReturn validation() throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			int i = RandomUtils.nextInt(10), j = RandomUtils.nextInt(10);
			String capText = i + "+" + j + "=?";
			BufferedImage bi = captchaProducer.createImage(capText);
			ImageIO.write(bi, "jpg", baos);
			//request.getSession().setAttribute(Constants.KEY_VALIDATION, String.valueOf(i + j));
			String tokenStr = StringUtils.systemUuid();
			redisTemplate.opsForHash().put(Constants.KEY_VALIDATION, tokenStr, String.valueOf(i + j));
			redisTemplate.expire(Constants.KEY_VALIDATION, 60, TimeUnit.SECONDS);
            return new AjaxReturn(200, tokenStr, Base64.getEncoder().encodeToString(baos.toByteArray()));
		}
	}
}
