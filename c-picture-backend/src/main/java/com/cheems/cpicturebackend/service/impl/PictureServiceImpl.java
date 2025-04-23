package com.cheems.cpicturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheems.cpicturebackend.exception.ErrorCode;
import com.cheems.cpicturebackend.exception.ThrowUtils;
import com.cheems.cpicturebackend.manager.FileManager;
import com.cheems.cpicturebackend.model.dto.picture.PictureQueryRequest;
import com.cheems.cpicturebackend.model.dto.picture.PictureUploadRequest;
import com.cheems.cpicturebackend.model.entity.User;
import com.cheems.cpicturebackend.model.vo.PictureVO;
import com.cheems.cpicturebackend.model.vo.UserVO;
import com.cheems.cpicturebackend.model.vo.file.UploadPictureResult;
import com.cheems.cpicturebackend.model.entity.Picture;
import com.cheems.cpicturebackend.service.PictureService;
import com.cheems.cpicturebackend.mapper.PictureMapper;
import com.cheems.cpicturebackend.service.UserService;
import com.qcloud.cos.model.UploadResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Resource
    private UserService userService;



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

    @Override
    public QueryWrapper getPictureQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        // 1. 获取参数， 判断不为空，则插入wapper
        QueryWrapper<Picture> pictureQueryWrapper = new QueryWrapper<>();
        if(pictureQueryRequest==null){
            return pictureQueryWrapper;
        }
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        Long userId = pictureQueryRequest.getUserId();
        String searchText = pictureQueryRequest.getSearchText();
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();

        if(StrUtil.isNotBlank(searchText)){
            pictureQueryWrapper.and(qw -> qw.like("name",searchText))
                    .or()
                    .like("introduction",searchText);
        }


        pictureQueryWrapper.eq(ObjUtil.isNotEmpty(id),"id",id);
        pictureQueryWrapper.like(ObjUtil.isNotEmpty(name),"name",name);
        pictureQueryWrapper.like(ObjUtil.isNotEmpty(introduction),"introduction",introduction);
        pictureQueryWrapper.eq(ObjUtil.isNotEmpty(category),"category",category);
        pictureQueryWrapper.eq(ObjUtil.isNotEmpty(picFormat),"picFormat",picFormat);
        pictureQueryWrapper.eq(ObjUtil.isNotEmpty(picSize),"picSize",picSize);
        pictureQueryWrapper.eq(ObjUtil.isNotEmpty(picWidth),"picWidth",picWidth);
        pictureQueryWrapper.eq(ObjUtil.isNotEmpty(picHeight),"picHeight",picHeight);
        pictureQueryWrapper.eq(ObjUtil.isNotEmpty(picScale),"picScale",picScale);
        pictureQueryWrapper.eq(ObjUtil.isNotEmpty(userId),"userId",userId);

        if(CollUtil.isNotEmpty(tags)){
            for (String tag : tags) {
                pictureQueryWrapper.like("tags","\""+tag+"\"");
            }
        }
        pictureQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField),sortOrder.equals("ascend"),sortField);
        return  pictureQueryWrapper;

    }

    /**
     * 这里还需要封装用户的信息
     * @param picture
     * @param httpServletRequest
     * @return
     */

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest httpServletRequest) {
        PictureVO pictureVO = PictureVO.objToVO(picture);
        Long userId = picture.getUserId();
        if(userId!=null){
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }

        return pictureVO;
    }

    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest httpServletRequest) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(),picturePage.getTotal());

        // todo think  这是在干啥
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVO).collect(Collectors.toList());
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());

        Map<Long,List<User>> userIdUserListMap  =userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));;

        pictureVOList.forEach(pictureVO -> {
            long userId = pictureVO.getUserId();
            User user = null;
            if(userIdUserListMap.containsKey(userId)){
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });

        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture==null ,ErrorCode.PARAMS_ERROR);

        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();

        ThrowUtils.throwIf(ObjUtil.isNull(id),ErrorCode.PARAMS_ERROR,"id不能为空");
        if(StrUtil.isNotBlank(url)){
            ThrowUtils.throwIf(url.length() >1024,ErrorCode.PARAMS_ERROR,"url过长");
        }
        if(StrUtil.isNotBlank(introduction)){
            ThrowUtils.throwIf(introduction.length() >800,ErrorCode.PARAMS_ERROR,"简介过长");
        }

    }
}




