package com.app.business.website.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.app.business.website.entity.Permissions;
import com.app.business.website.mapper.mysql.PermissionsMapper;
import com.app.business.website.service.PermissionsService;
import org.springframework.stereotype.Service;

/**
 * 权限表 服务层实现。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
@Service
public class PermissionsServiceImpl extends ServiceImpl<PermissionsMapper, Permissions>  implements PermissionsService{

}
