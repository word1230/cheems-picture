package com.cheems.cpicturebackend.controller;


import cn.hutool.json.JSONUtil;
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
import com.cheems.cpicturebackend.model.dto.picture.PictureEditRequest;
import com.cheems.cpicturebackend.model.dto.picture.PictureQueryRequest;
import com.cheems.cpicturebackend.model.dto.picture.PictureUpdateRequest;
import com.cheems.cpicturebackend.model.dto.picture.PictureUploadRequest;
import com.cheems.cpicturebackend.model.entity.Picture;
import com.cheems.cpicturebackend.model.entity.PictureTagCategory;
import com.cheems.cpicturebackend.model.entity.User;
import com.cheems.cpicturebackend.model.entity.UserAddRequest;
import com.cheems.cpicturebackend.model.vo.PictureVO;
import com.cheems.cpicturebackend.model.vo.UserLoginVO;
import com.cheems.cpicturebackend.model.vo.UserVO;
import com.cheems.cpicturebackend.service.PictureService;
import com.cheems.cpicturebackend.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import net.bytebuddy.implementation.bytecode.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
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
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile,
                                                 PictureUploadRequest pictureUploadRequest,
                                                 HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }


    /**
     * 图片下载接口
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download")
    public void testDownloadFile(String filepath, HttpServletResponse response){
        //1. 根据路径获取对象
        COSObjectInputStream cosObjectInputStream = null;
        try{
            COSObject cosObject = cosManager.getObject(filepath);
            //3.设置响应头
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filepath);
            //2. 将对象写入响应流中
            cosObjectInputStream = cosObject.getObjectContent();
            byte[] byteArray = IOUtils.toByteArray(cosObjectInputStream);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(byteArray);
            outputStream.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(cosObjectInputStream != null){
                try {
                    cosObjectInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    /**
     * 图片删除  仅管理员和  本人   可以删除  因此不能使用注解来权限校验了
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request ){
        //1. 校验参数
        ThrowUtils.throwIf(deleteRequest.getId() <=0 || deleteRequest == null,ErrorCode.PARAMS_ERROR);

        //2. 从数据库删除
        long id = deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);



        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null,ErrorCode.NOT_FOUND_ERROR);

        ThrowUtils.throwIf(!userService.isAdmin(loginUser) && !picture.getUserId().equals(loginUser.getId()),ErrorCode.NO_AUTH_ERROR);

        boolean b = pictureService.removeById(id);

        ThrowUtils.throwIf(!b,ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新图片(管理员)
     */

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest updateRequest){
        // 1. 校验参数
        ThrowUtils.throwIf(updateRequest.getId() <= 0 || updateRequest == null,ErrorCode.PARAMS_ERROR);

        //2. 构造picture对象
        ThrowUtils.throwIf(pictureService.getById(updateRequest.getId()) == null,ErrorCode.NOT_FOUND_ERROR);


        Picture picture = new Picture();
        BeanUtils.copyProperties(updateRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(updateRequest.getTags()));

        pictureService.validPicture(picture);



        //3. 更新数据库
        boolean b = pictureService.updateById(picture);
        ThrowUtils.throwIf(!b,ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据id获取图片所有信息（管理员）
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/get")
    public BaseResponse<Picture> getPictureById( Long id){
        ThrowUtils.throwIf(id <= 0 ,ErrorCode.PARAMS_ERROR);

        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null,ErrorCode.NOT_FOUND_ERROR);

        return ResultUtils.success(picture);
    }

    /**
     * 根据id 获取图片  （脱敏）
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById( Long id,HttpServletRequest request){
        ThrowUtils.throwIf(id <= 0 ,ErrorCode.PARAMS_ERROR);

        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null,ErrorCode.NOT_FOUND_ERROR);
        PictureVO pictureVO = pictureService.getPictureVO(picture, request);

        return ResultUtils.success(pictureVO);
    }


    /**
     * 分页获取图片列表（管理员）(所有信息)
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page")
    public  BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest){
        ThrowUtils.throwIf(pictureQueryRequest == null,ErrorCode.PARAMS_ERROR);
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();

        Page<Picture> picturePage = pictureService.page(new Page<>(current, pageSize),pictureService.getPictureQueryWrapper(pictureQueryRequest));

        return ResultUtils.success(picturePage);
    }


    /**
     * 分页获取图片列表(脱敏)
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page/vo")
    public  BaseResponse<Page<PictureVO>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest,HttpServletRequest request ){
        ThrowUtils.throwIf(pictureQueryRequest == null,ErrorCode.PARAMS_ERROR);
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();

        Page<Picture> page = pictureService.page(new Page<>(current, pageSize),pictureService.getPictureQueryWrapper(pictureQueryRequest));

        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(page, request);

        return ResultUtils.success(pictureVOPage);
    }

    /**
     * 编辑图片（仅本人与管理员可用）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest,HttpServletRequest request){
        ThrowUtils.throwIf(pictureEditRequest == null|| pictureEditRequest.getId() <=0,ErrorCode.PARAMS_ERROR);

        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        picture.setEditTime(new Date());

        pictureService.validPicture(picture);

        //验证权限
        User loginUser = userService.getLoginUser(request);
        if(!userService.isAdmin(loginUser) && ! loginUser.getId().equals(picture.getUserId())){
            throw  new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //判断图片存在
        Picture byId = pictureService.getById(pictureEditRequest.getId());
        ThrowUtils.throwIf(byId == null,ErrorCode.NOT_FOUND_ERROR);


        boolean b = pictureService.updateById(picture);
        ThrowUtils.throwIf(!b,ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(b);


    }

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }



}
