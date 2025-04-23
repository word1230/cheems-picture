package com.cheems.cpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cheems.cpicturebackend.model.dto.picture.PictureQueryRequest;
import com.cheems.cpicturebackend.model.dto.picture.PictureUploadRequest;
import com.cheems.cpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cheems.cpicturebackend.model.entity.User;
import com.cheems.cpicturebackend.model.vo.PictureVO;
import com.cheems.cpicturebackend.model.vo.file.UploadPictureResult;
import com.qcloud.cos.model.UploadResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author cheems
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-04-01 19:37:21
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */

    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser
                               );

    /**
     * 构造querywrapper
     */
    QueryWrapper getPictureQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片封装信息
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest httpServletRequest);

    /**
     * 分页结果封装
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest httpServletRequest);


    /**
     * 图片校验
     */
    void validPicture(Picture picture);

}
