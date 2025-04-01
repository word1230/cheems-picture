package com.cheems.cpicturebackend.aop;

import com.cheems.cpicturebackend.annotation.AuthCheck;
import com.cheems.cpicturebackend.constant.UserConstant;
import com.cheems.cpicturebackend.exception.BusinessException;
import com.cheems.cpicturebackend.exception.ErrorCode;
import com.cheems.cpicturebackend.model.entity.User;
import com.cheems.cpicturebackend.model.enums.UserRoleEnum;
import com.cheems.cpicturebackend.model.vo.UserLoginVO;
import com.cheems.cpicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验切面类
 */

@Aspect
@Component
public class AuthInterceptor {


    @Resource
    private UserService userService;

    /**
     * 一个切面， 在右authcheck注解时触发， 对权限校验
     */

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        //1. 拿到需要的权限 mustrole
        String mustRole = authCheck.mustRole();
        //2. 获取用户当前权限，
        RequestAttributes requestAttribute= RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttribute).getRequest();
        UserLoginVO currentUser = userService.getCurrentUser(request);
        String userRole = currentUser.getUserRole();
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(userRole);


        // 拿到mustrole 对应的权限的enum  需要 admin  但是 vip不行 如果 需要vip 但是你是admin 就可以
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        if(mustRoleEnum == null){
            return joinPoint.proceed();
        }

        //判断是否用户权限大于需要的权限
        if(userRoleEnum == null){
            throw  new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        if(userRoleEnum.getLevel() < mustRoleEnum.getLevel()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return joinPoint.proceed();




    }




}
