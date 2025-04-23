package com.cheems.cpicturebackend.controller;


import cn.hutool.core.util.StrUtil;
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
import com.cheems.cpicturebackend.model.dto.UserLoginRequest;
import com.cheems.cpicturebackend.model.dto.UserQueryRequest;
import com.cheems.cpicturebackend.model.dto.UserRegisterRequest;
import com.cheems.cpicturebackend.model.dto.UserUpdateRequest;
import com.cheems.cpicturebackend.model.entity.User;
import com.cheems.cpicturebackend.model.entity.UserAddRequest;
import com.cheems.cpicturebackend.model.vo.UserLoginVO;
import com.cheems.cpicturebackend.model.vo.UserVO;
import com.cheems.cpicturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long id = userService.UserRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(id);
    }

    @PostMapping("/login")
    public BaseResponse<UserLoginVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        UserLoginVO userLoginVO = userService.UserLogin(userAccount, userPassword, request);
        return ResultUtils.success(userLoginVO);
    }

    /**
     * 获取登录用户
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserLoginVO> getCurrentUser( HttpServletRequest request) {
        UserLoginVO currentUser = userService.getCurrentUser(request);
        return ResultUtils.success(currentUser);
    }

    @GetMapping("/logout")
    public BaseResponse<Boolean> userLogOut( HttpServletRequest request) {
        boolean b = userService.UserLogout(request);
        return ResultUtils.success(b);
    }


    /**
     * 创建用户
     * @param userAddRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    public BaseResponse<Long> userAdd(@RequestBody UserAddRequest userAddRequest) {

        ThrowUtils.throwIf(userAddRequest== null,ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest,user);
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean save = userService.save(user);
        ThrowUtils.throwIf(!save,ErrorCode.SUCCESS);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据id 获取用户
     * @param
     * @return
     */

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/get")
    public BaseResponse<User> getUserById(@RequestParam Long id) {

      ThrowUtils.throwIf(id == null,ErrorCode.PARAMS_ERROR);
      User user = userService.getById(id);
     ThrowUtils.throwIf(user == null,ErrorCode.NOT_FOUND_ERROR);
      return ResultUtils.success(user);
    }


    /**
     * 根据id 获取脱敏用户
     * @param
     * @return
     */

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(@RequestParam Long id) {

        ThrowUtils.throwIf(id == null,ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null,ErrorCode.NOT_FOUND_ERROR);

        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if(deleteRequest == null ||deleteRequest.getId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //逻辑删除
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    public BaseResponse<Boolean> UpdateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if(userUpdateRequest == null ||userUpdateRequest.getId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();

        BeanUtils.copyProperties(userUpdateRequest,user);

        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }



    /**
     * 分页查询数据
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> ListUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
       ThrowUtils.throwIf(userQueryRequest == null,ErrorCode.PARAMS_ERROR);

        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();

        Page<User>  page = new Page<>(current,pageSize);
        QueryWrapper<User> queryWrapper = userService.getQueryWrapper(userQueryRequest);
        Page<User> userPage = userService.page(page, queryWrapper);
        Page<UserVO> userVOPage = new Page<>(current,pageSize,userPage.getTotal());


        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());

        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);

    }




}
