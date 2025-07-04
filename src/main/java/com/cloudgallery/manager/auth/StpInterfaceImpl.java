package com.cloudgallery.manager.auth;

import cn.dev33.satoken.stp.StpInterface;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.cloudgallery.model.entity.Image;
import com.cloudgallery.model.entity.UserSpace;
import com.cloudgallery.service.ImageService;
import com.cloudgallery.service.UserSpaceService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 权限加载接口实现类
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    private static final Logger log = LoggerFactory.getLogger(StpInterfaceImpl.class);
    @Resource
    private UserSpaceService userSpaceService;
    @Resource
    private UserSpaceManager userSpaceManager;
    @Resource
    private ImageService imageService;

    /**
     * 根据用户ID获取对应账号体系下的权限集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 1.只判断space账号体系下的权限
        if (!loginType.equals("space")){
            return null;
        }
        // 2.获取登录用户的角色，查询UserSpace表
        UserSpaceAuthContext param = getParamId();
        // 2.1优先获取spaceId
        Long spaceId = param.getSpaceId();
        if (spaceId!=null){
            if (spaceId == 0) return userSpaceManager.getPermissionsByRole("owner");
            UserSpace userSpace = userSpaceService.lambdaQuery()
                    .eq(UserSpace::getSpaceId, spaceId)
                    .eq(UserSpace::getUserId, loginId)
                    .one();
            // UserSpace为空，则参数错误
            if (userSpace == null) return new ArrayList<>();
            String spaceRole = userSpace.getSpaceRole();
            List<String> permissionsByRole = userSpaceManager.getPermissionsByRole(spaceRole);
            return permissionsByRole;
        } else {
            Long imageId = param.getId();
            if (imageId == null)  return userSpaceManager.getPermissionsByRole("owner");
            Image image = imageService.lambdaQuery()
                    .eq(Image::getId, imageId)
                    .select(Image::getSpaceId)
                    .one();
            if (image == null) return new ArrayList<>();
            // 如果访问的是公共图库，返回管理员权限
            if (image.getSpaceId() == 0) return userSpaceManager.getPermissionsByRole("owner");
            UserSpace userSpace = userSpaceService.lambdaQuery()
                    .eq(UserSpace::getSpaceId, image.getSpaceId())
                    .eq(UserSpace::getUserId, loginId)
                    .one();
            // UserSpace为空，则参数错误
            if (userSpace == null) return new ArrayList<>();
            String spaceRole = userSpace.getSpaceRole();
            List<String> permissionsByRole = userSpaceManager.getPermissionsByRole(spaceRole);
            return permissionsByRole;
        }
    }

    /**
     * 根据用户ID获取对应账号体系下的角色集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return null;
    }

    /**
     * 直接从HttpServletRequest中获取空间ID/图片ID
     * @return
     */
    private UserSpaceAuthContext getParamId(){
        // 1. 获取请求参数
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 2.解析请求参数
        String method = request.getMethod();
        if ("POST".equals(method)) {
            String body = null;
            try {
                body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                log.info("body: {}", body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return JSONUtil.toBean(body, UserSpaceAuthContext.class);
        } else {
            Map<String, String[]> parameterMap = request.getParameterMap();
            UserSpaceAuthContext context = new UserSpaceAuthContext();
            // 手动赋值并做类型转换
            if (parameterMap.containsKey("spaceId")) {
                context.setSpaceId(Long.valueOf(parameterMap.get("spaceId")[0]));
            }
            if (parameterMap.containsKey("id")) {
                context.setId(Long.valueOf(parameterMap.get("id")[0]));
            }
            return context;
        }
    }

}
