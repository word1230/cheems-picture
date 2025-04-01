package com.cheems.cpicturebackend.controller;

import com.cheems.cpicturebackend.annotation.AuthCheck;
import com.cheems.cpicturebackend.common.BaseResponse;
import com.cheems.cpicturebackend.common.ResultUtils;
import com.cheems.cpicturebackend.constant.UserConstant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class MainController {

    @AuthCheck(mustRole = UserConstant.VIP_ROLE)
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("OK");
    }
}
