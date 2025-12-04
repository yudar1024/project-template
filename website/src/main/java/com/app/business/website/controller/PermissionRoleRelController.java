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
import com.app.business.website.entity.PermissionRoleRel;
import com.app.business.website.service.PermissionRoleRelService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 *  控制层。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
@RestController
@RequestMapping("/permissionRoleRel")
public class PermissionRoleRelController {

    @Autowired
    private PermissionRoleRelService permissionRoleRelService;

    /**
     * 保存。
     *
     * @param permissionRoleRel 
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody PermissionRoleRel permissionRoleRel) {
        return permissionRoleRelService.save(permissionRoleRel);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return permissionRoleRelService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param permissionRoleRel 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody PermissionRoleRel permissionRoleRel) {
        return permissionRoleRelService.updateById(permissionRoleRel);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<PermissionRoleRel> list() {
        return permissionRoleRelService.list();
    }

    /**
     * 根据主键获取。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public PermissionRoleRel getInfo(@PathVariable Long id) {
        return permissionRoleRelService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<PermissionRoleRel> page(Page<PermissionRoleRel> page) {
        return permissionRoleRelService.page(page);
    }

}
