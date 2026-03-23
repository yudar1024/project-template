// file: src/main/java/com/gcl/app/application/dto/request/MenuUpdateRequest.java
package com.gcl.app.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单更新请求
 */
@Data
public class MenuUpdateRequest {

    @NotNull(message = "菜单ID不能为空")
    private Long id;

    private String menuName;

    private String menuPath;

    private Long parentId;

    private Integer level;

    private Integer sort;

    private String icon;
}
