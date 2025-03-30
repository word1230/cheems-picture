package com.cheems.cpicturebackend.common;

import lombok.Data;

@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页数
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序规则(默认升序)
     */
    private String sortOrder = "descend";


}
