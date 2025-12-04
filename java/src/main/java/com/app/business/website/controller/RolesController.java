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
import com.app.business.website.entity.Roles;
import com.app.business.website.service.RolesService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 角色表 控制层。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
@RestController
@RequestMapping("/roles")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    /**
     * 保存角色表。
     *
     * @param roles 角色表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Roles roles) {
        return rolesService.save(roles);
    }

    /**
     * 根据主键删除角色表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return rolesService.removeById(id);
    }

    /**
     * 根据主键更新角色表。
     *
     * @param roles 角色表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Roles roles) {
        return rolesService.updateById(roles);
    }

    /**
     * 查询所有角色表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Roles> list() {
        return rolesService.list();
    }

    /**
     * 根据主键获取角色表。
     *
     * @param id 角色表主键
     * @return 角色表详情
     */
    @GetMapping("getInfo/{id}")
    public Roles getInfo(@PathVariable Long id) {
        return rolesService.getById(id);
    }

    /**
     * 分页查询角色表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Roles> page(Page<Roles> page) {
        return rolesService.page(page);
    }

}
