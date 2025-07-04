package com.cloudgallery.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudgallery.common.RoleUtil;
import com.cloudgallery.common.ThrowUtil;
import com.cloudgallery.exception.BusinessException;
import com.cloudgallery.exception.ErrorCode;
import com.cloudgallery.manager.ai.ImageDescriptionManager;
import com.cloudgallery.manager.ai.SpringAiManager;
import com.cloudgallery.manager.cos.CosManager;
import com.cloudgallery.manager.cos.FileUploadManager;
import com.cloudgallery.manager.cos.ImageUploadTemplate;
import com.cloudgallery.manager.cos.UrlUploadManager;
import com.cloudgallery.mapper.ImageMapper;
import com.cloudgallery.model.dto.image.ImageQueryDto;
import com.cloudgallery.model.dto.image.ImageUpdateDto;
import com.cloudgallery.model.dto.image.ImageUploadDto;
import com.cloudgallery.model.entity.Image;
import com.cloudgallery.model.entity.Space;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.vo.ImageVO;
import com.cloudgallery.service.ImageService;
import com.cloudgallery.service.SpaceService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 图片服务实现类
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService{

    @Resource
    @Lazy
    private FileUploadManager fileUploadManager;
    @Resource
    @Lazy
    private UrlUploadManager urlUploadManager;
    @Resource
    private CosManager cosManager;
    @Resource
    private SpringAiManager springAiManager;
    @Resource
    private SpaceService spaceService;

    /**
     * 上传图片实现类
     * @param dataSource
     * @param imageUploadDto
     * @param loginUser
     */
    @Override
    public ImageVO uploadImage(Object dataSource, ImageUploadDto imageUploadDto, User loginUser) throws IOException {
        // 1.判断数据来源
        ImageUploadTemplate imageUploadTemplate = fileUploadManager;
        if (dataSource instanceof String) imageUploadTemplate = urlUploadManager;
        // 2.上传文件
        Long spaceId = imageUploadDto.getSpaceId();
        if (spaceId!= null && spaceId > 0) {
            Space space = spaceService.lambdaQuery().eq(Space::getId, spaceId).one();
            // 空间权限校验
            ThrowUtil.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            ThrowUtil.throwIf(!space.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限");
            // 空间额度校验
            ThrowUtil.throwIf(space.getSize()> space.getMaxSize()||space.getNumber()> space.getMaxNumber(),
                    ErrorCode.PARAMS_ERROR, "空间容量已满");
        }
        ImageVO imageVO = imageUploadTemplate.uploadImage(dataSource, loginUser, imageUploadDto);
        return imageVO;
    }

    /**
     * 构建查询条件
     * @param imageQueryDto
     */
    @Override
    public Wrapper<Image> getQueryWrapper(ImageQueryDto imageQueryDto) {
        // 1.分页信息
        String sortField = imageQueryDto.getSortField();
        String sortOrder = imageQueryDto.getSortOrder();
        Long id = imageQueryDto.getId();
        String name = imageQueryDto.getName();
        String url = imageQueryDto.getUrl();
        Long userId = imageQueryDto.getUserId();
        String description = imageQueryDto.getDescription();
        String label = imageQueryDto.getLabel();
        Long spaceId = imageQueryDto.getSpaceId();
        Long size = imageQueryDto.getSize();
        Integer width = imageQueryDto.getWidth();
        Integer status = imageQueryDto.getStatus();
        Integer auditStatus = imageQueryDto.getAuditStatus();
        Integer height = imageQueryDto.getHeight();
        Date createTime = imageQueryDto.getCreateTime();
        // 2.构建查询条件
        QueryWrapper<Image> queryWrapper = new QueryWrapper<Image>()
                .eq(id != null, "id", id)
                .like(name != null, "name", name)
                .like(url != null, "url", url)
                .eq(userId != null, "userId", userId)
                .like(description != null, "description", description)
                .like(label != null, "label", label)
                .eq(size != null, "size", size)
                .eq(width != null, "width", width)
                .eq(height != null, "height", height)
                .eq(status != null, "status", status)
                .eq(auditStatus != null, "audit_status", auditStatus)
                .eq(spaceId!= null, "space_id", spaceId)
                .eq(createTime != null, "create_time", createTime);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),sortOrder.equals("scend"), sortField);
        return queryWrapper;
    }

    /**
     * 删除图片
     * @param id
     */
    @Override
    public Boolean deleteImageById(Long id,User loginUser) {
        // 1.获取图片信息
        Image image = getById(id);
        ThrowUtil.throwIf(image == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        // 2.权限校验，只能修改自己的信息（当前登录用户ID==参数ID||当前登录用户为管理员）
        RoleUtil.verifyRole(loginUser, image.getUserId());
        // 3.删除图片
        boolean deleteDBFile = removeById(id);
        boolean deleteCosFile = cosManager.deleteFile(image.getUrl());
        return deleteDBFile && deleteCosFile;
    }

    /**
     * 修改图片
     * @param imageUpdateDto
     */
    @Override
    public ImageVO updateImage(ImageUpdateDto imageUpdateDto, User loginUser) {
        // 1.获取图片信息
        Image image = getById(imageUpdateDto.getId());
        ThrowUtil.throwIf(image == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        // 2.权限校验，只能修改自己的信息（当前登录用户ID==参数ID||当前登录用户为管理员）
        RoleUtil.verifyRole(loginUser, image.getUserId());
        // 3.更新图片
        String name = imageUpdateDto.getName();
        String description = imageUpdateDto.getDescription();
        String label = imageUpdateDto.getLabel();
        if (name != null) image.setName(name);
        if (description != null) image.setDescription(description);
        if (label != null) image.setLabel(String.join(",", label));
        image.setUpdateTime(new Date());
        boolean b = updateById(image);
        ThrowUtil.throwIf(!b, ErrorCode.OPERATION_ERROR, "更新图片失败");
        return ImageVO.imageToImageVO(image);
    }

    /**
     * 审核通过图片
     * @param id
     */
    @Override
    public Boolean auditPassImage(Long id,User loginUser) {
        Image image = getById(id);
        ThrowUtil.throwIf(image == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        image.setAuditStatus(1);
        image.setAuditId(loginUser.getId());
        return updateById(image);
    }

    /**
     * 审核拒绝图片
     * @param id
     * @param reason
     * @param loginUser
     */
    @Override
    public Boolean auditRejectImage(Long id, String reason, User loginUser) {
        Image image = getById(id);
        ThrowUtil.throwIf(image == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        // 设置图片审核状态和拒绝原因
        image.setAuditStatus(2);
        image.setAuditId(loginUser.getId());
        image.setAuditReason(reason);
        return updateById(image);
    }

    /**
     * 文生图
     * @param prompt
     */
    @Override
    public ImageVO generateImage(String prompt, User loginUser) throws IOException {
        String url = springAiManager.generateImage(prompt);
        ThrowUtil.throwIf(url == null, ErrorCode.OPERATION_ERROR, "生成图片失败");
        ImageUploadDto imageUploadDto = new ImageUploadDto();
        imageUploadDto.setName(prompt);
        imageUploadDto.setUrl(url);
        ImageVO imageVO = uploadImage(url, imageUploadDto, loginUser);
        return imageVO;
    }

    @Override
    public void downloadImage(Long id, HttpServletResponse response) throws IOException {
        Image image = getById(id);
        ThrowUtil.throwIf(image == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");

        COSObject cosObject = cosManager.downloadFile(image.getUrl());
        if (cosObject == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件在COS中不存在");
        }

        try (COSObjectInputStream objectContent = cosObject.getObjectContent()) {
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setContentLengthLong(cosObject.getObjectMetadata().getContentLength());

            String encodedFilename = URLEncoder.encode(image.getName(), StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=\"%s\"; filename*=UTF-8''%s", encodedFilename, encodedFilename));

            // 使用缓冲流式传输
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = objectContent.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }

            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("下载图片失败: {}", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "下载图片失败");
        }
    }

    @Override
    public ImageVO generateDescription(Long imageId, User loginUser) throws IOException, InterruptedException {
        Image image = getById(imageId);
        ThrowUtil.throwIf(image == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        // 调用API生成图片描述
        String descriptionPrompt = """
        请生成一段图片相关的描述，在30字左右。
        要求：描述内容与图片内容一致；
        """;
        String description = ImageDescriptionManager.callApiWithImage(image.getUrl(),descriptionPrompt);
        ThrowUtil.throwIf(description == null||description=="", ErrorCode.OPERATION_ERROR, "生成图片描述失败");
        ThrowUtil.throwIf(image.getUserId()!=loginUser.getId(), ErrorCode.NO_AUTH_ERROR,"生成描述："+description);
        // 生成图片标签
        String labelPrompt = """
        请分析这张图片，并输出一组描述图片内容的关键词标签。要求：
        1. 标签之间用英文逗号分隔；
        2. 不超过 5 个标签；
        3. 包括但不限于以下类别：主体对象、场景、颜色、风格、动作等；
        4. 示例格式：风景,动物,绿色,自然,户外
        """;
        String label = ImageDescriptionManager.callApiWithImage(image.getUrl(),labelPrompt);
        ThrowUtil.throwIf(label == null||label=="", ErrorCode.OPERATION_ERROR, "生成图片标签失败");
        ThrowUtil.throwIf(image.getUserId()!=loginUser.getId(), ErrorCode.NO_AUTH_ERROR,"生成标签："+label);
        image.setDescription(description);
        image.setLabel(label);
        updateById(image);
        return ImageVO.imageToImageVO(image);
    }


}




