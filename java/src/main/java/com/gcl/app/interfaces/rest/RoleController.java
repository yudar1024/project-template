// file: src/main/java/com/gcl/app/interfaces/rest/RoleController.java
package com.gcl.app.interfaces.rest;

import com.gcl.app.application.dto.request.RoleCreateRequest;
import com.gcl.app.application.dto.request.RoleUpdateRequest;
import com.gcl.app.application.dto.response.RoleResponse;
import com.gcl.app.application.service.RoleService;
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
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/list")
    public ApiResponse<List<RoleResponse>> list() {
        List<RoleResponse> result = roleService.listRoles();
        return ApiResponse.success(result);
    }

    @PostMapping("/add")
    public ApiResponse<RoleResponse> add(@Valid @RequestBody RoleCreateRequest request) {
        RoleResponse response = roleService.createRole(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/update")
    public ApiResponse<RoleResponse> update(@Valid @RequestBody RoleUpdateRequest request) {
        RoleResponse response = roleService.updateRole(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success();
    }
}
