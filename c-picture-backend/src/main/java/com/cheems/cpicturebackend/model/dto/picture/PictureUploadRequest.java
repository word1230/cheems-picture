package com.cheems.cpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;
}
