package com.app.business.website.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.app.business.website.entity.Users;
import com.app.business.website.mapper.mysql.UsersMapper;
import com.app.business.website.service.UsersService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author CodeGenerator
 * @since 2025-10-30
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>  implements UsersService{

}
