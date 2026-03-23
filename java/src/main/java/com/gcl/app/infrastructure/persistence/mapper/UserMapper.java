// file: src/main/java/com/gcl/app/infrastructure/persistence/mapper/UserMapper.java
package com.gcl.app.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gcl.app.infrastructure.persistence.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
