package com.app.business.website.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.app.business.website.entity.Permissions;
import com.app.business.website.service.PermissionsService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 权限表 控制层。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
@RestController
@RequestMapping("/permissions")
public class PermissionsController {

    @Autowired
    private PermissionsService permissionsService;

    /**
     * 保存权限表。
     *
     * @param permissions 权限表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Permissions permissions) {
        return permissionsService.save(permissions);
    }

    /**
     * 根据主键删除权限表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return permissionsService.removeById(id);
    }

    /**
     * 根据主键更新权限表。
     *
     * @param permissions 权限表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Permissions permissions) {
        return permissionsService.updateById(permissions);
    }

    /**
     * 查询所有权限表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Permissions> list() {
        return permissionsService.list();
    }

    /**
     * 根据主键获取权限表。
     *
     * @param id 权限表主键
     * @return 权限表详情
     */
    @GetMapping("getInfo/{id}")
    public Permissions getInfo(@PathVariable Long id) {
        return permissionsService.getById(id);
    }

    /**
     * 分页查询权限表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Permissions> page(Page<Permissions> page) {
        return permissionsService.page(page);
    }

}
