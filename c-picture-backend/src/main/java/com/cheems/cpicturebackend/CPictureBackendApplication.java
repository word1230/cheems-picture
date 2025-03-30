package com.cheems.cpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.cheems.cpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)  //
public class CPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CPictureBackendApplication.class, args);
    }

}
