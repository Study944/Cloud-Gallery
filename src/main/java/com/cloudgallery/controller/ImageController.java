package com.cloudgallery.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudgallery.annotation.UserRole;
import com.cloudgallery.common.BaseResponse;
import com.cloudgallery.common.ResultUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.model.dto.image.ImageQueryDto;
import com.cloudgallery.model.dto.image.ImageUpdateDto;
import com.cloudgallery.model.dto.image.ImageUploadDto;
import com.cloudgallery.model.entity.Image;
import com.cloudgallery.model.entity.Space;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.ImageVO;
import com.cloudgallery.service.ImageService;
import com.cloudgallery.service.SpaceService;
import com.cloudgallery.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Resource
    private ImageService imageService;
    @Resource
    private UserService userService;
    @Resource
    private SpaceService spaceService;

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

    /**
     * 下载图片--TODO 未完成
     * @param id
     * @param response
     */
    @GetMapping("/download")
    @UserRole(role = "user")
    public BaseResponse<String> downloadImage(@RequestParam("id") Long id, @RequestParam HttpServletResponse  response) throws IOException {
        imageService.downloadImage(id, response);
        return ResultUtil.success("");
    }

    /**
     * 分页获取图片列表--普通用户
     * @param imageQueryDto
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ImageVO>> listImageVOByPage(@RequestBody ImageQueryDto imageQueryDto,HttpServletRequest request){
        // 用户只能获取审核通过的图片
        imageQueryDto.setAuditStatus(1);
        imageQueryDto.setStatus(1);
        // 如果访问私人空间图片，需要条件：1.登录；2.参数空间ID的用户ID等于当前登录用户ID
        if (imageQueryDto.getSpaceId()!=null&&imageQueryDto.getSpaceId()!=0L){
            User loginUser = userService.getLoginUser(request);
            Space space = spaceService.lambdaQuery().
                    eq(Space::getId, imageQueryDto.getSpaceId()).
                    one();
            ThrowUtil.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR,"空间不存在");
            ThrowUtil.throwIf(!space.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR,"无权限");
        }
        if (imageQueryDto.getSpaceId()== null) imageQueryDto.setSpaceId(0L);
        Page<Image>  page= imageService.page(
                new Page<>(imageQueryDto.getCurrent(), imageQueryDto.getPageSize()),
                imageService.getQueryWrapper(imageQueryDto));
        Page<ImageVO> imageVOPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<ImageVO> imageVOList = page.getRecords()
                .stream()
                .map(ImageVO::imageToImageVO)
                .collect(Collectors.toList());
        imageVOPage.setRecords(imageVOList);
        return ResultUtil.success(imageVOPage);
    }

    /**
     * 获取图片列表--管理员
     * @param imageQueryDto
     * @return
     */
    @PostMapping("/list/page")
    @UserRole(role = "admin")
    public BaseResponse<Page<Image>> listImageByPage(@RequestBody ImageQueryDto imageQueryDto){
        Page<Image>  page= imageService.page(
                new Page<>(imageQueryDto.getCurrent(), imageQueryDto.getPageSize()),
                imageService.getQueryWrapper(imageQueryDto));
        return ResultUtil.success(page);
    }
    /**
     * 根据id获取图片
     * @param id
     */
    @GetMapping("/getById")
    public BaseResponse<ImageVO> getImageById(@RequestParam("id") Long id){
        Image image = imageService.getById(id);
        ThrowUtil.throwIf(image == null, ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        ImageVO imageVO = ImageVO.imageToImageVO(image);
        return ResultUtil.success(imageVO);
    }
    /**
     * 删除图片
     * @param id
     */
    @GetMapping("/deleteById")
    @UserRole(role = "user")
    public BaseResponse<String> deleteImageById(@RequestParam("id") Long id, HttpServletRequest request){
        ThrowUtil.throwIf(id == null, ErrorCode.PARAMS_ERROR,"id不能为空");
        User loginUser = userService.getLoginUser(request);
        Boolean result  = imageService.deleteImageById(id, loginUser);
        return ResultUtil.success(result ? "删除成功" : "删除失败");
    }

    /**
     * 修改图片
     * @param imageUpdateDto
     */
    @PostMapping("/update")
    @UserRole(role = "user")
    public BaseResponse<ImageVO> updateImage(@RequestBody ImageUpdateDto imageUpdateDto, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        ImageVO result = imageService.updateImage(imageUpdateDto, loginUser);
        return ResultUtil.success(result);
    }

    /**
     * 审核通过图片
     * @param id
     */
    @GetMapping("/audit/pass")
    @UserRole(role = "admin")
    public BaseResponse<String> auditPassImage(Long id , HttpServletRequest request){
        ThrowUtil.throwIf(id == null, ErrorCode.PARAMS_ERROR,"id不能为空");
        User loginUser = userService.getLoginUser(request);
        Boolean result = imageService.auditPassImage(id, loginUser);
        return ResultUtil.success(result ? "审核成功" : "审核失败");
    }

    /**
     * 拒绝图片
     * @param id
     * @param reason
     * @param request
     */
    @GetMapping("/audit/reject")
    @UserRole(role = "admin")
    public BaseResponse<String> auditRejectImage(Long id, String reason, HttpServletRequest request){
        ThrowUtil.throwIf(id == null, ErrorCode.PARAMS_ERROR,"id不能为空");
        ThrowUtil.throwIf(reason == null, ErrorCode.PARAMS_ERROR,"reason不能为空");
        User loginUser = userService.getLoginUser(request);
        Boolean result = imageService.auditRejectImage(id,reason, loginUser);
        return ResultUtil.success(result ? "审核成功" : "审核失败");
    }

    /**
     * AI生成图片
     * @param prompt
     * @param request
     */
    @GetMapping("/generateImage")
    public BaseResponse<ImageVO> generateImages(String prompt, HttpServletRequest request) throws IOException {
        User loginUser = userService.getLoginUser(request);
        ImageVO result = imageService.generateImage(prompt, loginUser);
        return ResultUtil.success(result);
    }

    /**
     * AI生成图片描述
     * @param imageId
     * @param request
     */
    @GetMapping("/generateDescription")
    public BaseResponse<ImageVO> generateDescription(Long imageId,HttpServletRequest request) throws Exception {
        User loginUser = userService.getLoginUser(request);
        ImageVO result = imageService.generateDescription(imageId, loginUser);
        return ResultUtil.success(result);
    }
}
