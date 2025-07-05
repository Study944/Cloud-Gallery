package com.cloudgallery.manager.websocket;

import com.cloudgallery.manager.auth.UserSpaceManager;
import com.cloudgallery.model.entity.Image;
import com.cloudgallery.model.entity.Space;
import com.cloudgallery.model.entity.User;
import com.cloudgallery.model.entity.UserSpace;
import com.cloudgallery.model.enums.SpaceRoleEnums;
import com.cloudgallery.model.enums.SpaceTypeEnums;
import com.cloudgallery.service.ImageService;
import com.cloudgallery.service.SpaceService;
import com.cloudgallery.service.UserService;
import com.cloudgallery.service.UserSpaceService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 握手拦截器
 */
@Slf4j
@Component
public class WsHandShakeInterceptor implements HandshakeInterceptor {

    @Resource
    private UserService userService;
    @Resource
    private ImageService imageService;
    @Resource
    private SpaceService spaceService;
    @Resource
    private UserSpaceService userSpaceService;

    /**
     * 握手前操作校验用户权限和信息获取
     * @param request 请求
     * @param response  响应
     * @param wsHandler 处理器
     * @param attributes
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 1.解析ServerHttpRequest
        HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        // 2.判断握手条件是否满足
        // 2.1用户需要登录
        User loginUser = userService.getLoginUser(httpServletRequest);
        if (loginUser == null) {
            log.error("用户未登录");
            return false;
        }
        // 2.2指定操作的图片
        String imageId = httpServletRequest.getParameter("id");
        if (imageId == null) {
            log.error("图片Id为空");
            return false;
        }
        Image image = imageService.getById(imageId);
        if (image == null) {
            log.error("图片不存在");
            return false;
        }
        // 2.3指定团队空间
        Long spaceId = image.getSpaceId();
        Space space = spaceService.lambdaQuery()
                .eq(Space::getId, spaceId)
                .eq(Space::getSpaceType, SpaceTypeEnums.GROUP)
                .one();
        if (space == null){
            log.error("非团队空间");
            return false;
        }
        // 2.4判断用户是否是空间成员
        UserSpace userSpace = userSpaceService.lambdaQuery()
                .eq(UserSpace::getSpaceId, spaceId)
                .eq(UserSpace::getUserId, loginUser.getId())
                .one();
        if (userSpace == null){
            log.error("非空间成员");
            return false;
        }
        // 2.5判断用户权限
        String spaceRole = userSpace.getSpaceRole();
        if (spaceRole.equals(SpaceRoleEnums.VIEWER)){
            log.error("用户无编辑权限");
            return false;
        }
        // 3.保存用户连接信息
        attributes.put("user", loginUser);
        attributes.put("userId", loginUser.getId());
        attributes.put("imageId", imageId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
