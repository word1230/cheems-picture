package com.cheems.cpicturebackend.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 * @TableName user
 */
@Data
public class UserUpdateRequest implements Serializable {

    private Long id;


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

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