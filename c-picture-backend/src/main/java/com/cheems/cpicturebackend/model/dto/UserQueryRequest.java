package com.cheems.cpicturebackend.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.cheems.cpicturebackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;


    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/vip/admin
     */
    private String userRole;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
