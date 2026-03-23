// file: src/main/java/com/gcl/app/application/dto/request/RoleUpdateRequest.java
package com.gcl.app.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色更新请求
 */
@Data
public class RoleUpdateRequest {

    @NotNull(message = "角色ID不能为空")
    private Long id;

    private String roleName;

    private List<Long> rolePermissionList;
}
