// file: src/main/java/com/gcl/app/interfaces/rest/PermissionController.java
package com.gcl.app.interfaces.rest;

import com.gcl.app.application.dto.request.PermissionCreateRequest;
import com.gcl.app.application.dto.request.PermissionUpdateRequest;
import com.gcl.app.application.dto.response.PermissionResponse;
import com.gcl.app.application.service.PermissionService;
import com.gcl.app.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/list")
    public ApiResponse<List<PermissionResponse>> list() {
        List<PermissionResponse> result = permissionService.listPermissions();
        return ApiResponse.success(result);
    }

    @PostMapping("/add")
    public ApiResponse<PermissionResponse> add(
            @Valid @RequestBody PermissionCreateRequest request) {
        PermissionResponse response = permissionService.createPermission(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/update")
    public ApiResponse<PermissionResponse> update(
            @Valid @RequestBody PermissionUpdateRequest request) {
        PermissionResponse response = permissionService.updatePermission(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success();
    }
}
