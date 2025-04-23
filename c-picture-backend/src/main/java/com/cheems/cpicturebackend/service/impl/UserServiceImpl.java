package com.cheems.cpicturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheems.cpicturebackend.constant.UserConstant;
import com.cheems.cpicturebackend.exception.BusinessException;
import com.cheems.cpicturebackend.exception.ErrorCode;
import com.cheems.cpicturebackend.exception.ThrowUtils;
import com.cheems.cpicturebackend.model.dto.UserQueryRequest;
import com.cheems.cpicturebackend.model.entity.User;
import com.cheems.cpicturebackend.model.enums.UserRoleEnum;
import com.cheems.cpicturebackend.model.vo.UserLoginVO;
import com.cheems.cpicturebackend.model.vo.UserVO;
import com.cheems.cpicturebackend.service.UserService;
import com.cheems.cpicturebackend.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author cheems
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-03-31 18:38:20
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Override
    public long UserRegister(String userAccount, String userPassword, String checkPassword) {
        //1. 检查参数
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //密码大于8位
        //用户名大于4位
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码太短");
        }
        ThrowUtils.throwIf(userAccount.length()<4,ErrorCode.PARAMS_ERROR,"账号太短");

        ThrowUtils.throwIf(!userPassword.equals(checkPassword),ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");



        //2.查询是否账号重复

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        Long l = this.baseMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(l>0,ErrorCode.PARAMS_ERROR,"账号重复");
        //3. 对密码加密
        String encryptPassword = this.getEncryptPassword(userPassword);
        //4. 存入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());

        boolean b = this.saveOrUpdate(user);
        ThrowUtils.throwIf(!b,ErrorCode.SYSTEM_ERROR,"系统异常");
        return user.getId();

    }

    @Override
    public UserLoginVO UserLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1. 检查参数
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //密码大于8位
        //用户名大于4位
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码太短");
        }
        ThrowUtils.throwIf(userAccount.length()<4,ErrorCode.PARAMS_ERROR,"账号太短");


        String encryptPassword = this.getEncryptPassword(userPassword);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(user==null,ErrorCode.PARAMS_ERROR,"账号不存在");

        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);

        return this.getUserLoginVO(user);

    }

    @Override
    public UserLoginVO getCurrentUser(HttpServletRequest request) {
        //1. 从session 中拿到user对象

        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) attribute;

        //2. 判断不为空
        ThrowUtils.throwIf(user==null,ErrorCode.NOT_LOGIN_ERROR);

        //3. 根据id 从数据库中查询一次

        Long id = user.getId();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        User currentuser = this.getBaseMapper().selectOne(queryWrapper);

        //4.将脱敏数据返回
        return this.getUserLoginVO(currentuser);

    }

    @Override
    public boolean UserLogout(HttpServletRequest request) {
        //1. 拿到登录用户

        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) attribute;
        //2. 判断不为空
        ThrowUtils.throwIf(user==null,ErrorCode.NOT_LOGIN_ERROR);



        //3. 将session 中的用户信息移除
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        //4. 返回true
        return true;





    }


    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }



    @Override
    public UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> users) {
        if(CollUtil.isEmpty(users)){
            return new ArrayList<>();
        }
      return   users.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        final String SALT ="cheems";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        return encryptPassword;
    }

    @Override
    public UserLoginVO getUserLoginVO(User user) {
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(user,userLoginVO);
        return userLoginVO;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) attribute;
        return user;
    }


}




