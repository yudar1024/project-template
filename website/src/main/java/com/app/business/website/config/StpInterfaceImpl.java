package com.app.business.website.config;

import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo;
import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of("user-read", "user-write", "user-delete");
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of("user","admin");
    }
}
