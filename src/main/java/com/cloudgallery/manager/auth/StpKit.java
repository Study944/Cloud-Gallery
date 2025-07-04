package com.cloudgallery.manager.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 * 实现区分多账号体系的权限校验区分
 */
public class StpKit {

    /**
     * 默认原生会话对象
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * UserSpace会话对象，管理 团队空间 下所有账号的登录、权限认证
     */
    public static final StpLogic SPACE = new StpLogic("space");


}
