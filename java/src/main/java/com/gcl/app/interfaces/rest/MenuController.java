// file: src/main/java/com/gcl/app/interfaces/rest/MenuController.java
package com.gcl.app.interfaces.rest;

import com.gcl.app.application.dto.request.MenuCreateRequest;
import com.gcl.app.application.dto.request.MenuUpdateRequest;
import com.gcl.app.application.dto.response.MenuResponse;
import com.gcl.app.application.service.MenuService;
import com.gcl.app.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.dev33.satoken.stp.StpUtil;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/list")
    public ApiResponse<List<MenuResponse>> list() {
        List<MenuResponse> result = menuService.listMenus();
        return ApiResponse.success(result);
    }

    @GetMapping("/my")
    public ApiResponse<List<MenuResponse>> my() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<MenuResponse> result = menuService.listAuthorizedMenus(userId);
        return ApiResponse.success(result);
    }

    @PostMapping("/add")
    public ApiResponse<MenuResponse> add(@Valid @RequestBody MenuCreateRequest request) {
        MenuResponse response = menuService.createMenu(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/update")
    public ApiResponse<MenuResponse> update(@Valid @RequestBody MenuUpdateRequest request) {
        MenuResponse response = menuService.updateMenu(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long id) {
        menuService.deleteMenu(id);
        return ApiResponse.success();
    }
}
