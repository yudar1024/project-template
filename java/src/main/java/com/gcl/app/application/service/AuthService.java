// file: src/main/java/com/gcl/app/application/service/AuthService.java
package com.gcl.app.application.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gcl.app.application.dto.request.LoginRequest;
import com.gcl.app.application.dto.response.LoginResponse;
import com.gcl.app.common.exception.BusinessException;
import com.gcl.app.common.exception.ErrorCode;
import com.gcl.app.common.util.PasswordUtil;
import com.gcl.app.infrastructure.persistence.entity.UserEntity;
import com.gcl.app.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 认证服务 — 处理登录/登出
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserMapper userMapper;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        UserEntity user = userMapper.selectOne(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, request.getUserName())
        );
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 校验密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // Sa-Token 登录（以用户ID为登录标识）
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        log.info("用户登录成功, userId={}, username={}", user.getId(), user.getUsername());

        return LoginResponse.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .token(token)
                .build();
    }

    /**
     * 用户登出
     */
    public void logout() {
        StpUtil.logout();
        log.info("用户登出成功");
    }
}
