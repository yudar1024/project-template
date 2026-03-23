// file: src/main/java/com/gcl/app/infrastructure/persistence/mapper/PermissionMenuMapper.java
package com.gcl.app.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gcl.app.infrastructure.persistence.entity.PermissionMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMenuMapper extends BaseMapper<PermissionMenuEntity> {

    @Select("SELECT menu_id FROM permission_menus WHERE permission_id = #{permissionId}")
    List<Long> selectMenuIdsByPermissionId(@Param("permissionId") Long permissionId);
}
