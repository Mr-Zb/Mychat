package com.fun.api.controller;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.fun.framework.domain.AjaxReturn;
import com.fun.framework.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Slf4j
@Api(tags="图片上传")
@RestController
@RequestMapping("/api")
public class ImgController extends BaseController {

    @Value("${basedir}")
    private String basedir;
    @Value("${uploadLinux}")
    private String uploadLinux;

    //
    @Value("${endpoint}")
    private String endpoint;

    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;

    @Value("${returnUrl}")
    private String returnUrl;


    @ApiOperation(value = "图片上传 【通用】" )
    @RequestMapping(path = { "/img1" }, method = { RequestMethod.POST })
    public AjaxReturn  fileUpload2(@RequestParam("files") MultipartFile files) throws IOException {
        String path = System.currentTimeMillis()+files.getOriginalFilename();
        String path2=uploadLinux+path;
        File newFile=new File(path2);
        files.transferTo(newFile);
        return new AjaxReturn(200,"上传成功！",basedir+path);
    }

    @ApiOperation(value = "图片上传 【通用】" )
    @RequestMapping(path = { "/img" }, method = { RequestMethod.POST })
    public AjaxReturn  fileUpload(@RequestParam("files") MultipartFile files) throws IOException {
        String path = System.currentTimeMillis()+files.getOriginalFilename();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest("javaban", "feixin/"+path, files.getInputStream());
        try {
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("捕获到一个OSSException，这意味着您的请求已发送到OSS，但由于某种原因被拒绝并返回错误响应。");
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("捕获到客户端异常，这意味着客户端在尝试与OSS通信时遇到严重的内部问题，例如无法访问网络.");
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return new AjaxReturn(200,"上传成功！",returnUrl+path);
    }
}
