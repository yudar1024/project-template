// file: src/main/java/com/gcl/app/infrastructure/persistence/mapper/RolePermissionMapper.java
package com.gcl.app.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gcl.app.infrastructure.persistence.entity.RolePermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionEntity> {

    @Select("SELECT permission_id FROM role_permissions WHERE role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
