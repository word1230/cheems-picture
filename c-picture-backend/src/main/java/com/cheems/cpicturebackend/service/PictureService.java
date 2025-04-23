package com.cheems.cpicturebackend.service;

import com.cheems.cpicturebackend.model.dto.picture.PictureUploadRequest;
import com.cheems.cpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cheems.cpicturebackend.model.entity.User;
import com.cheems.cpicturebackend.model.vo.PictureVO;
import com.cheems.cpicturebackend.model.vo.file.UploadPictureResult;
import com.qcloud.cos.model.UploadResult;
import org.springframework.web.multipart.MultipartFile;

/**
* @author cheems
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-04-01 19:37:21
*/
public interface PictureService extends IService<Picture> {


    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser
                               );
}
