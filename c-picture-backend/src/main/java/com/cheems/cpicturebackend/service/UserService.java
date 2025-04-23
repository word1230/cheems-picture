package com.cheems.cpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cheems.cpicturebackend.model.dto.UserQueryRequest;
import com.cheems.cpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cheems.cpicturebackend.model.vo.UserLoginVO;
import com.cheems.cpicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author cheems
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-03-31 18:38:20
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */

    long UserRegister(String userAccount, String userPassword,String checkPassword);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    UserLoginVO UserLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    UserLoginVO getCurrentUser(HttpServletRequest request);

    /**
     * 用户注销
     * @param userPassword
     * @return
     */
    boolean UserLogout(HttpServletRequest request);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    UserVO getUserVO(User user);

    List<UserVO> getUserVOList(List<User> users);


    String getEncryptPassword(String userPassword);

    UserLoginVO getUserLoginVO(User user);


    User getLoginUser(HttpServletRequest request);
}
