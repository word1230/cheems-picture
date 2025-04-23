package com.cheems.cpicturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.cheems.cpicturebackend.config.CosClientConfig;
import com.cheems.cpicturebackend.exception.ErrorCode;
import com.cheems.cpicturebackend.exception.ThrowUtils;
import com.cheems.cpicturebackend.model.vo.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileManager {

    @Resource
    private CosManager cosManager;


    @Resource
    private CosClientConfig cosClientConfig;



    final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "png", "jpg","webp");


    /**
    编写上传图片的方法
    prefix 是为了以后可能有私有空间，团队空间等，用前缀区分
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile,String uploadPathPrefix) {
        /*
        1. 校验图片
        2. 拼接图片上传地址
        3. 上传
        4.将返回结果
         */
        validPicture(multipartFile);

        //2. 拼接图片上传名称 与 路径    为了管理方便 将时间戳也拼进去

        UUID uuid = UUID.randomUUID();
        String originalFilename = multipartFile.getOriginalFilename();
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),uuid,FileUtil
                .getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix,uploadFilename);
        File file =null;
        try{
            file = File.createTempFile(uploadPath,null);
            multipartFile.transferTo(file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();


            int width = imageInfo.getWidth();
            int height = imageInfo.getHeight();
            double picScale = NumberUtil.round(width*1.0/height,2).doubleValue();


            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost()+"/"+ uploadPath);
            uploadPictureResult.setName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicWidth(width);
            uploadPictureResult.setPicHeight(height);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            return uploadPictureResult;
        } catch (IOException e) {
            log.error("图片上传对象存储失败",e);
            throw new RuntimeException(e);
        }finally {
            this.deleteTempFile(file);
        }


    }

    /**
     * 删除临时文件
     * @param file
     */

    private void deleteTempFile(File file) {
        if(file !=null && file.exists()){
            boolean delete = file.delete();
            if(!delete){
                log.error("file delete fail filepath:{}",file.getAbsolutePath());
            }
        }
    }


    /**
     * 校验图片
     * @param multipartFile
     */
    public  void  validPicture(MultipartFile multipartFile) {
        /*
        1. 校验文件大小
        2.校验文件文件后缀    拿到一个列表，判断是否在其中
         */
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR,"文件不能为空");
        long fileSize = multipartFile.getSize();
        final long ONE_MB = 1024 * 1024;
        ThrowUtils.throwIf(fileSize > 2*ONE_MB,ErrorCode.PARAMS_ERROR,"文件大小不能超过2M");



        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(suffix),ErrorCode.PARAMS_ERROR,"文件格式不正确");

    }



}
