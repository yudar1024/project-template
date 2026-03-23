// file: src/main/java/com/gcl/app/infrastructure/persistence/mapper/UserRoleMapper.java
package com.gcl.app.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gcl.app.infrastructure.persistence.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

    @Select("SELECT role_id FROM user_roles WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}
