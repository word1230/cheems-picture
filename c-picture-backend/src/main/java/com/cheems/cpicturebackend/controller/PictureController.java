package com.cheems.cpicturebackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cheems.cpicturebackend.annotation.AuthCheck;
import com.cheems.cpicturebackend.common.BaseResponse;
import com.cheems.cpicturebackend.common.DeleteRequest;
import com.cheems.cpicturebackend.common.ResultUtils;
import com.cheems.cpicturebackend.constant.UserConstant;
import com.cheems.cpicturebackend.exception.BusinessException;
import com.cheems.cpicturebackend.exception.ErrorCode;
import com.cheems.cpicturebackend.exception.ThrowUtils;
import com.cheems.cpicturebackend.manager.CosManager;
import com.cheems.cpicturebackend.manager.FileManager;
import com.cheems.cpicturebackend.model.dto.UserLoginRequest;
import com.cheems.cpicturebackend.model.dto.UserQueryRequest;
import com.cheems.cpicturebackend.model.dto.UserRegisterRequest;
import com.cheems.cpicturebackend.model.dto.UserUpdateRequest;
import com.cheems.cpicturebackend.model.dto.picture.PictureUploadRequest;
import com.cheems.cpicturebackend.model.entity.User;
import com.cheems.cpicturebackend.model.entity.UserAddRequest;
import com.cheems.cpicturebackend.model.vo.PictureVO;
import com.cheems.cpicturebackend.model.vo.UserLoginVO;
import com.cheems.cpicturebackend.model.vo.UserVO;
import com.cheems.cpicturebackend.service.PictureService;
import com.cheems.cpicturebackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/picture")
public class PictureController {

    private static final Logger log = LoggerFactory.getLogger(PictureController.class);
    @Resource
    private CosManager cosManager;

    @Resource
    private PictureService pictureService;

    @Resource
    private FileManager fileManager;


    @Resource
    private UserService userService;

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadPicture(MultipartFile multipartFile) {
        /*
         1. 接收数据
         2. 设定存储路径， 名称
         3. 调用cosmanager进行上传
         */
        String filename = multipartFile.getOriginalFilename();
        //拼接路径
        String filepath =String.format("/test/%s",filename);
        File file = null;

        try{
            //新建文件， 将接收到的文件的数据转存进去，然后存储
            file = File.createTempFile(filepath,null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath,file);
            return ResultUtils.success(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            //删除掉临时文件
            if(file != null){
                boolean delete = file.delete();
                if(!delete){
                    log.error("file delete fail， filepath:{}",filepath);
                }
            }
        }


    }


    /**
     * 图片上传接口
     */
    //todo 这里返回之后没有将图片的id返回
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile,
                                                 PictureUploadRequest pictureUploadRequest,
                                                 HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }




}
