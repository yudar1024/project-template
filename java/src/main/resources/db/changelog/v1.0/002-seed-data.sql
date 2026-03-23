--liquibase formatted sql

--changeset admin:002-seed-data splitStatements:true
--comment: 初始化管理员用户、角色、权限、菜单及关联数据

-- 初始管理员用户 (密码: admin123, BCrypt加密)
INSERT INTO
    java_auth.users (
        username,
        phone,
        email,
        password,
        nickname,
        status,
        created_at,
        updated_at,
        created_by,
        updated_by
    )
VALUES (
        'admin',
        '13800000000',
        'admin@example.com',
        '$2a$10$U/X99jr.FrJerKsRG2obTeug9H28NYspUXgNbcojp/8cwPwAO/c72',
        '系统管理员',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-23 09:53:53',
        NULL,
        NULL
    ),
    (
        'roger',
        '18733293829',
        'roger@163.com',
        '$2a$10$eIXxSpN2nCTq95hnHsxIBuN.MOF5ap3JeOi6dAG5mQXNzOje/ITHW',
        'roger',
        'active',
        '2026-03-23 10:17:34',
        '2026-03-23 13:44:18',
        NULL,
        NULL
    );

-- 初始角色
INSERT INTO
    roles (name, status)
VALUES ('admin', 'active'),
    ('user', 'active');

-- 初始权限
INSERT INTO
    java_auth.permissions (
        name,
        code,
        `type`,
        status,
        created_at,
        updated_at,
        created_by,
        updated_by
    )
VALUES (
        '用户列表',
        'user:list',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '新增用户',
        'user:add',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '更新用户',
        'user:update',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '删除用户',
        'user:delete',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '角色列表',
        'role:list',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '新增角色',
        'role:add',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '更新角色',
        'role:update',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '删除角色',
        'role:delete',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '权限列表',
        'permission:list',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '新增权限',
        'permission:add',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    );

INSERT INTO
    java_auth.permissions (
        name,
        code,
        `type`,
        status,
        created_at,
        updated_at,
        created_by,
        updated_by
    )
VALUES (
        '更新权限',
        'permission:update',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '删除权限',
        'permission:delete',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '菜单列表',
        'menu:list',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '新增菜单',
        'menu:add',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '更新菜单',
        'menu:update',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '删除菜单',
        'menu:delete',
        'api',
        'active',
        '2026-03-19 16:52:19',
        '2026-03-19 16:52:19',
        NULL,
        NULL
    ),
    (
        '菜单管理',
        'menu:menumgt',
        'menu',
        'active',
        '2026-03-23 10:56:35',
        '2026-03-23 10:56:35',
        NULL,
        NULL
    ),
    (
        '用户管理',
        'menu:usermgt',
        'menu',
        'active',
        '2026-03-23 11:02:57',
        '2026-03-23 11:02:57',
        NULL,
        NULL
    ),
    (
        '角色管理',
        'menu:rolemgmt',
        'menu',
        'active',
        '2026-03-23 11:02:57',
        '2026-03-23 11:02:57',
        NULL,
        NULL
    ),
    (
        '权限管理',
        'menu:permissoinmgt',
        'menu',
        'active',
        '2026-03-23 11:02:57',
        '2026-03-23 11:02:57',
        NULL,
        NULL
    );

INSERT INTO
    java_auth.permissions (
        name,
        code,
        `type`,
        status,
        created_at,
        updated_at,
        created_by,
        updated_by
    )
VALUES (
        '系统管理',
        'menu:systemmgt',
        'menu',
        'active',
        '2026-03-23 11:05:05',
        '2026-03-23 11:05:05',
        NULL,
        NULL
    );

-- 初始菜单
INSERT INTO
    menus (
        name,
        path,
        parent_id,
        level,
        sort,
        icon,
        status
    )
VALUES (
        '系统管理',
        '/system',
        0,
        1,
        1,
        'SettingOutlined',
        'active'
    ),
    (
        '用户管理',
        '/usermgt',
        1,
        2,
        1,
        'UserOutlined',
        'active'
    ),
    (
        '角色管理',
        '/role',
        1,
        2,
        2,
        'TeamOutlined',
        'active'
    ),
    (
        '权限管理',
        '/permission',
        1,
        2,
        3,
        'SafetyOutlined',
        'active'
    ),
    (
        '菜单管理',
        '/menu',
        1,
        2,
        4,
        'MenuOutlined',
        'active'
    );

-- admin角色拥有所有权限
INSERT INTO
    java_auth.role_permissions (role_id, permission_id)
VALUES (1, 1),
    (2, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (1, 5),
    (2, 5),
    (1, 6),
    (1, 7),
    (1, 8);

INSERT INTO
    java_auth.role_permissions (role_id, permission_id)
VALUES (1, 9),
    (2, 9),
    (1, 10),
    (1, 11),
    (1, 12),
    (1, 13),
    (2, 13),
    (1, 14),
    (1, 15),
    (1, 16);

INSERT INTO
    java_auth.role_permissions (role_id, permission_id)
VALUES (1, 17),
    (2, 17),
    (1, 18),
    (2, 18),
    (1, 19),
    (2, 19),
    (1, 20),
    (2, 20),
    (1, 21),
    (2, 21);

-- admin用户关联admin角色
INSERT INTO
    java_auth.user_roles (user_id, role_id)
VALUES (1, 1),
    (2, 2);

-- 所有权限关联对应菜单
INSERT INTO
    java_auth.permission_menus (permission_id, menu_id)
VALUES (21, 1),
    (18, 2),
    (19, 3),
    (20, 4),
    (17, 5);