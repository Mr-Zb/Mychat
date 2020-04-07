package com.fun.api.aliyunOSS;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class AliyunUtil {

    @Value("${endpoint}")
    private String endpoint;

    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;

    @Value("${returnUrlUser}")
    private String returnUrlUser;

    @Value("${userImg}")
    private String userImg;

    @Value("${groupImg}")
    private String groupImg;

    public String getUserImg(){
        return userImg;
    }

    public String getGroupImg(){
        return groupImg;
    }

    public String aliyun(InputStream inputStream,String name) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest("javaban", "userimg/"+name, inputStream);
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
        return returnUrlUser+name;
    }
}
