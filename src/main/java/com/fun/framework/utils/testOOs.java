package com.fun.framework.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class testOOs {

    public static void main(String[] args) throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "LTAI4FurPfuFAxhDobmghrnj";
        String accessKeySecret = "XUOTT8kHVopUSV55xrFg3Qk8N6llB9";

// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 上传文件流。
        File file = new File("/Users/mac/Desktop/img/userLittle.jpg");
        InputStream inputStream = new FileInputStream(file);
        ossClient.putObject("javaban", "feixin/bb.png", inputStream);
// 关闭OSSClient。
        ossClient.shutdown();
        //https://javaban.oss-cn-hangzhou.aliyuncs.com/aa/bb.png
    }
}
