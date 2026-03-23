// file: src/main/java/com/gcl/app/infrastructure/persistence/mapper/MenuMapper.java
package com.gcl.app.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gcl.app.infrastructure.persistence.entity.MenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {

    @Select("SELECT DISTINCT m.* " +
            "FROM menus m " +
            "JOIN permission_menus pm ON m.id = pm.menu_id " +
            "JOIN permissions p ON pm.permission_id = p.id " +
            "JOIN role_permissions rp ON p.id = rp.permission_id " +
            "JOIN user_roles ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} " +
            "  AND p.type = 'menu' " +
            "  AND m.status = 'active' " +
            "  AND p.status = 'active' " +
            "ORDER BY m.sort ASC, m.id ASC")
    List<MenuEntity> selectAuthorizedMenusByUserId(@Param("userId") Long userId);
}
