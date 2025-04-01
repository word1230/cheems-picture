package com.cheems.cpicturebackend.model.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Getter

public enum UserRoleEnum {


    USER("普通用户","user",1),
    VIP("会员用户","vip",2),
    ADMIN("管理员","admin",3);





    private String text;

    private String value;

    private int level;

    UserRoleEnum(String text, String value, int level) {
        this.text = text;
        this.value = value;
        this.level = level;
    }

    private static final Map<String, UserRoleEnum> collect = Arrays.stream(UserRoleEnum.values()).collect(Collectors.toMap(UserRoleEnum::getValue, userRoleEnum -> userRoleEnum));


    public static UserRoleEnum getEnumByValue(String value) {
        //1. 转为一个map value 与enum的map
        // 判断与value 相同， 则将enum 返回

        //2. 否则

        if(StrUtil.isBlank(value)){
            return null;
        }

        return collect.getOrDefault(value, null);

    }
}
