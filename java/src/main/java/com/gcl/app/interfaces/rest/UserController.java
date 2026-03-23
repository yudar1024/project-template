// file: src/main/java/com/gcl/app/interfaces/rest/UserController.java
package com.gcl.app.interfaces.rest;

import com.gcl.app.application.dto.request.UserCreateRequest;
import com.gcl.app.application.dto.request.UserUpdateRequest;
import com.gcl.app.application.dto.response.UserResponse;
import com.gcl.app.application.service.UserService;
import com.gcl.app.common.response.ApiResponse;
import com.gcl.app.common.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public ApiResponse<PageResponse<UserResponse>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        PageResponse<UserResponse> result = userService.listUsers(page, size);
        return ApiResponse.success(result);
    }

    @PostMapping("/add")
    public ApiResponse<UserResponse> add(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/update")
    public ApiResponse<UserResponse> update(@Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long id) {
        userService.deleteUser(id);
        return ApiResponse.success();
    }
}
