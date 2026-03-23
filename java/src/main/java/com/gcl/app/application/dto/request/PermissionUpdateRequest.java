// file: src/main/java/com/gcl/app/application/dto/request/PermissionUpdateRequest.java
package com.gcl.app.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 权限更新请求
 */
@Data
public class PermissionUpdateRequest {

    @NotNull(message = "权限ID不能为空")
    private Long id;

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "权限名只能为字母、数字、下划线")
    private String permissionName;

    private String permissionExp;

    private String version;

    @Pattern(regexp = "^(api|menu)$", message = "权限类型只能为api或menu")
    private String permissionType;
}
