// file: src/main/java/com/gcl/app/application/dto/request/PermissionCreateRequest.java
package com.gcl.app.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 权限创建请求
 */
@Data
public class PermissionCreateRequest {

    @NotBlank(message = "权限名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "权限名只能为字母、数字、下划线")
    private String permissionName;

    @NotBlank(message = "权限表达式不能为空")
    private String permissionExp;

    @NotBlank(message = "权限版本不能为空")
    private String version;

    @NotBlank(message = "权限类型不能为空")
    @Pattern(regexp = "^(api|menu)$", message = "权限类型只能为api或menu")
    private String permissionType;
}
