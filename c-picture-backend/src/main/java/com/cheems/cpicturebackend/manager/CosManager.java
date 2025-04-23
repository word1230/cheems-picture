package com.cheems.cpicturebackend.manager;

import com.cheems.cpicturebackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.File;

/**
 * 对cosclient 进一步封装
 * 实现文件上传与下载方法
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 腾讯云sdk包中有的
     */
    @Resource
    private COSClient cosClient;


    /**
     * 实现文件上传的封装
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 文件下载
     * @param key
     * @return
     */

    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }


    /**
     * 上传文件（附带图片信息）
     */

    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);

        //对文件进行处理， 获取文件信息
        PicOperations picOperations = new PicOperations();
        //0 不返回原图信息 1 返回原图信息 默认为0
        picOperations.setIsPicInfo(1);
        putObjectRequest.setPicOperations(picOperations);


        return cosClient.putObject(putObjectRequest);

    }


}
