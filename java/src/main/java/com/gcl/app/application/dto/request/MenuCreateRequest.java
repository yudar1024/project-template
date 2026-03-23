// file: src/main/java/com/gcl/app/application/dto/request/MenuCreateRequest.java
package com.gcl.app.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单创建请求
 */
@Data
public class MenuCreateRequest {

    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @NotBlank(message = "菜单路径不能为空")
    private String menuPath;

    @NotNull(message = "父菜单ID不能为空")
    private Long parentId;

    @NotNull(message = "菜单层级不能为空")
    private Integer level;

    private Integer sort;

    private String icon;
}
