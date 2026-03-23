// file: src/main/java/com/gcl/app/application/dto/response/MenuResponse.java
package com.gcl.app.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单响应（支持树形结构）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    private Long id;
    private String menuName;
    private String menuPath;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private String icon;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
    private List<MenuResponse> children;
}
