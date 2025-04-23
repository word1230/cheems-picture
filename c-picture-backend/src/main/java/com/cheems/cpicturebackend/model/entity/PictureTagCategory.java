package com.cheems.cpicturebackend.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class PictureTagCategory {

    private List<String> categoryList;
    private List<String> tagList;

}
