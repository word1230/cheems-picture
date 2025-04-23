package com.cheems.cpicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheems.cpicturebackend.exception.ErrorCode;
import com.cheems.cpicturebackend.exception.ThrowUtils;
import com.cheems.cpicturebackend.manager.FileManager;
import com.cheems.cpicturebackend.model.dto.picture.PictureUploadRequest;
import com.cheems.cpicturebackend.model.entity.User;
import com.cheems.cpicturebackend.model.vo.PictureVO;
import com.cheems.cpicturebackend.model.vo.file.UploadPictureResult;
import com.cheems.cpicturebackend.model.entity.Picture;
import com.cheems.cpicturebackend.service.PictureService;
import com.cheems.cpicturebackend.mapper.PictureMapper;
import com.qcloud.cos.model.UploadResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author cheems
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-04-01 19:37:21
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{


    @Resource
    private FileManager fileManager;



    /**
     *  图片上传
     */
    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {

        /*
        判断是更新还是新增图片  如果是新增则id 一定为空， 不是则不为空，获取到id
        1. 调用FileManager方法
        2. 存到数据库里

         */

        ThrowUtils.throwIf(loginUser==null, ErrorCode.NOT_LOGIN_ERROR);
        //获取pictureId
        Long pictureId = null;
        if(pictureUploadRequest!=null){
            pictureId =pictureUploadRequest.getId();
        }

        //判断是否是更新
        if(pictureId!=null){
            boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists();
            ThrowUtils.throwIf(!exists,ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        }



        String  uploadPathPrefix = String.format("public/%s",loginUser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);

        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());


        //如果是更新， 则将id 设置过去 ，
        if(pictureId!=null){
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR,"图片上传失败");


        return  PictureVO.objToVO(picture);
    }
}




