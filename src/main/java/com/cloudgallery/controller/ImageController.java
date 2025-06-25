package com.cloudgallery.controller;

import com.cloudgallery.annotation.UserRole;
import com.cloudgallery.common.BaseResponse;
import com.cloudgallery.common.ResultUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.model.dto.image.ImageUploadDto;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.ImageVO;
import com.cloudgallery.service.ImageService;
import com.cloudgallery.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Resource
    private ImageService imageService;
    @Resource
    private UserService userService;

    /**
     * 图片文件上传
     * @param multipartFile
     * @param imageUploadDto
     * @param request
     */
    @UserRole(role = "user")
    @PostMapping("/upload/file")
    public BaseResponse<ImageVO> uploadImage(@RequestParam("file") MultipartFile multipartFile,
                                             ImageUploadDto imageUploadDto,
                                             HttpServletRequest request) throws IOException {
        ThrowUtil.throwIf(multipartFile.isEmpty(), ErrorCode.PARAMS_ERROR,"上传文件不能为空");
        User loginUser = userService.getLoginUser(request);
        ImageVO result = imageService.uploadImage(multipartFile, imageUploadDto, loginUser);
        return ResultUtil.success(result);
    }

    /**
     * 图片URL上传
     * @param imageUploadDto
     * @param request
     */
    @UserRole(role = "user")
    @PostMapping("/upload/url")
    public BaseResponse<ImageVO> uploadImageByUrl(@RequestBody ImageUploadDto imageUploadDto,
                                                  HttpServletRequest request) throws IOException {
        String url = imageUploadDto.getUrl();
        ThrowUtil.throwIf(url == null, ErrorCode.PARAMS_ERROR,"上传url不能为空");
        User loginUser = userService.getLoginUser(request);
        ImageVO result = imageService.uploadImage(url, imageUploadDto, loginUser);
        return ResultUtil.success(result);
    }

}
