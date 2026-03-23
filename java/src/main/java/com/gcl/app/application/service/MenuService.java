// file: src/main/java/com/gcl/app/application/service/MenuService.java
package com.gcl.app.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gcl.app.application.dto.request.MenuCreateRequest;
import com.gcl.app.application.dto.request.MenuUpdateRequest;
import com.gcl.app.application.dto.response.MenuResponse;
import com.gcl.app.common.exception.BusinessException;
import com.gcl.app.common.exception.ErrorCode;
import com.gcl.app.infrastructure.persistence.entity.MenuEntity;
import com.gcl.app.infrastructure.persistence.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单管理服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuMapper menuMapper;

    /**
     * 菜单树列表
     */
    public List<MenuResponse> listMenus() {
        List<MenuEntity> allMenus = menuMapper.selectList(
                new LambdaQueryWrapper<MenuEntity>()
                        .orderByAsc(MenuEntity::getSort)
                        .orderByAsc(MenuEntity::getId)
        );
        return buildMenuTree(allMenus);
    }

    /**
     * 当前用户授权的菜单树列表
     */
    public List<MenuResponse> listAuthorizedMenus(Long userId) {
        List<MenuEntity> authorizedMenus = menuMapper.selectAuthorizedMenusByUserId(userId);
        return buildMenuTree(authorizedMenus);
    }

    /**
     * 创建菜单
     */
    @Transactional
    public MenuResponse createMenu(MenuCreateRequest request) {
        MenuEntity entity = new MenuEntity();
        entity.setName(request.getMenuName());
        entity.setPath(request.getMenuPath());
        entity.setParentId(request.getParentId());
        entity.setLevel(request.getLevel());
        entity.setSort(request.getSort());
        entity.setIcon(request.getIcon());
        entity.setStatus("active");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        menuMapper.insert(entity);

        log.info("菜单创建成功, menuId={}, name={}", entity.getId(), entity.getName());
        return toResponse(entity);
    }

    /**
     * 更新菜单
     */
    @Transactional
    public MenuResponse updateMenu(MenuUpdateRequest request) {
        MenuEntity existing = menuMapper.selectById(request.getId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在: " + request.getId());
        }

        if (request.getMenuName() != null) {
            existing.setName(request.getMenuName());
        }
        if (request.getMenuPath() != null) {
            existing.setPath(request.getMenuPath());
        }
        if (request.getParentId() != null) {
            existing.setParentId(request.getParentId());
        }
        if (request.getLevel() != null) {
            existing.setLevel(request.getLevel());
        }
        if (request.getSort() != null) {
            existing.setSort(request.getSort());
        }
        if (request.getIcon() != null) {
            existing.setIcon(request.getIcon());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        menuMapper.updateById(existing);

        log.info("菜单更新成功, menuId={}", existing.getId());
        return toResponse(existing);
    }

    /**
     * 删除菜单
     */
    @Transactional
    public void deleteMenu(Long id) {
        MenuEntity existing = menuMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在: " + id);
        }

        // 检查是否有子菜单
        long childCount = menuMapper.selectCount(
                new LambdaQueryWrapper<MenuEntity>()
                        .eq(MenuEntity::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "该菜单下还有子菜单，请先删除子菜单");
        }

        menuMapper.deleteById(id);
        log.info("菜单删除成功, menuId={}", id);
    }

    /**
     * 构建菜单树
     */
    private List<MenuResponse> buildMenuTree(List<MenuEntity> allMenus) {
        List<MenuResponse> responseList = allMenus.stream()
                .map(this::toResponse)
                .toList();

        Map<Long, List<MenuResponse>> groupByParent = responseList.stream()
                .collect(Collectors.groupingBy(MenuResponse::getParentId));

        // 为每个节点设置子节点
        for (MenuResponse menu : responseList) {
            menu.setChildren(groupByParent.getOrDefault(menu.getId(), new ArrayList<>()));
        }

        // 返回顶层节点（parentId = 0）
        return groupByParent.getOrDefault(0L, new ArrayList<>());
    }

    private MenuResponse toResponse(MenuEntity entity) {
        return MenuResponse.builder()
                .id(entity.getId())
                .menuName(entity.getName())
                .menuPath(entity.getPath())
                .parentId(entity.getParentId())
                .level(entity.getLevel())
                .sort(entity.getSort())
                .icon(entity.getIcon())
                .createTime(entity.getCreatedAt())
                .updateTime(entity.getUpdatedAt())
                .createUser(entity.getCreatedBy())
                .updateUser(entity.getUpdatedBy())
                .build();
    }
}
