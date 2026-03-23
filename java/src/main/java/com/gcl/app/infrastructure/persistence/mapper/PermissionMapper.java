// file: src/main/java/com/gcl/app/infrastructure/persistence/mapper/PermissionMapper.java
package com.gcl.app.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gcl.app.infrastructure.persistence.entity.PermissionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {
}
