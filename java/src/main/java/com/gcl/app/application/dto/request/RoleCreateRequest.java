// file: src/main/java/com/gcl/app/application/dto/request/RoleCreateRequest.java
package com.gcl.app.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 角色创建请求
 */
@Data
public class RoleCreateRequest {

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    private List<Long> rolePermissionList;
}
