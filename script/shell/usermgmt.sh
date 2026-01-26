#!/bin/bash

# ====================================================
# 用户管理脚本
# 功能：
# 1. 交互式输入参数
# 2. 支持操作类型：删除用户、新增用户、修改用户组
# 3. 新增用户时创建home目录，加入指定组，设置默认密码
# 作者：Linux系统管理员
# ====================================================

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 打印彩色信息
info() { echo -e "${BLUE}[INFO]${NC} $1"; }
success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }
question() { echo -e "${CYAN}[QUESTION]${NC} $1"; }

# 默认密码
DEFAULT_PASSWORD="SG!1644"

# 检查是否以root运行
check_root() {
    if [[ $EUID -ne 0 ]]; then
        error "请使用root权限运行此脚本"
        echo "使用: sudo bash $0"
        exit 1
    fi
}

# 检查用户是否存在
user_exists() {
    local username="$1"
    id "$username" &>/dev/null
    return $?
}

# 检查组是否存在
group_exists() {
    local groupname="$1"
    getent group "$groupname" &>/dev/null
    return $?
}

# 检查用户是否在组中
user_in_group() {
    local username="$1"
    local groupname="$2"
    
    if ! user_exists "$username"; then
        return 1
    fi
    
    if ! group_exists "$groupname"; then
        return 1
    fi
    
    groups "$username" | grep -q "\b$groupname\b"
    return $?
}

# 显示主菜单
show_menu() {
    clear
    echo "================================================"
    echo "            用户管理系统"
    echo "================================================"
    echo "请选择要执行的操作："
    echo "  1) 新增用户"
    echo "  2) 删除用户"
    echo "  3) 修改用户组"
    echo "  4) 显示所有用户"
    echo "  5) 显示所有组"
    echo "  6) 退出"
    echo "================================================"
}

# 新增用户
add_user() {
    echo ""
    info "开始新增用户流程..."
    
    # 获取home目录路径
    while true; do
        read -p "请输入用户home目录路径 [默认: /home]: " home_path
        home_path=${home_path:-/home}
        
        # 检查路径是否存在
        if [ ! -d "$home_path" ]; then
            question "目录 $home_path 不存在，是否创建？ (y/n) [默认: y]: "
            read create_dir
            create_dir=${create_dir:-y}
            
            case "$create_dir" in
                [Yy]*|"")
                    mkdir -p "$home_path"
                    if [ $? -eq 0 ]; then
                        success "目录 $home_path 创建成功"
                        break
                    else
                        error "目录创建失败，请检查权限"
                    fi
                    ;;
                [Nn]*)
                    warning "请重新输入一个已存在的目录"
                    ;;
                *)
                    warning "无效选择，请重新输入"
                    ;;
            esac
        else
            break
        fi
    done
    
    # 循环添加用户
    while true; do
        echo ""
        # 获取用户名
        while true; do
            read -p "请输入用户名: " username
            
            if [ -z "$username" ]; then
                warning "用户名不能为空"
                continue
            fi
            
            if user_exists "$username"; then
                warning "用户 $username 已存在，请使用其他用户名"
                continue
            fi
            
            # 检查用户名是否有效（只允许字母、数字、下划线、连字符）
            if [[ ! "$username" =~ ^[a-zA-Z0-9_-]+$ ]]; then
                warning "用户名只能包含字母、数字、下划线和连字符"
                continue
            fi
            
            break
        done
        
        # 创建用户
        user_home="$home_path/$username"
        info "正在创建用户: $username"
        info "Home目录: $user_home"
        
        # 创建用户，设置home目录，不创建邮箱，设置默认shell为bash
        useradd -m -d "$user_home" -s /bin/bash "$username"
        
        if [ $? -ne 0 ]; then
            error "用户创建失败"
            return 1
        fi
        
        success "用户 $username 创建成功"
        
        # 设置默认密码
        echo "$username:$DEFAULT_PASSWORD" | chpasswd
        
        if [ $? -eq 0 ]; then
            success "密码设置成功"
        else
            warning "密码设置失败，可能需要手动设置"
        fi
        
        # 设置密码永不过期，不要求首次登录修改密码
        chage -m 0 -M 99999 -I -1 -E -1 "$username"
        
        if [ $? -eq 0 ]; then
            success "密码策略设置成功"
        else
            warning "密码策略设置失败"
        fi
        
        # 将用户加入sudo组
        if group_exists "sudo"; then
            usermod -aG sudo "$username"
            success "用户 $username 已加入sudo组"
        else
            warning "sudo组不存在，跳过"
        fi
        
        # 将用户加入adm组
        if group_exists "adm"; then
            usermod -aG adm "$username"
            success "用户 $username 已加入adm组"
        else
            warning "adm组不存在，跳过"
        fi
        
        # 将用户加入docker组（如果存在）
        if group_exists "docker"; then
            usermod -aG docker "$username"
            success "用户 $username 已加入docker组"
        else
            info "docker组不存在，跳过"
        fi
        
        # 设置home目录权限
        chmod 755 "$user_home"
        chown -R "$username:$username" "$user_home"
        
        # 显示用户信息
        echo ""
        info "用户 $username 创建完成，详细信息："
        echo "=========================================="
        echo "用户名: $username"
        echo "Home目录: $user_home"
        echo "默认密码: $DEFAULT_PASSWORD"
        echo "所属组: $(groups $username)"
        echo "=========================================="
        
        # 询问是否继续添加用户
        echo ""
        question "是否继续添加其他用户？ (y/n) [默认: n]: "
        read continue_add
        continue_add=${continue_add:-n}
        
        case "$continue_add" in
            [Yy]*)
                info "继续添加新用户..."
                ;;
            [Nn]*|"")
                info "返回主菜单"
                break
                ;;
            *)
                warning "无效选择，返回主菜单"
                break
                ;;
        esac
    done
}

# 删除用户
delete_user() {
    echo ""
    info "开始删除用户流程..."
    
    # 获取用户名
    while true; do
        read -p "请输入要删除的用户名: " username
        
        if [ -z "$username" ]; then
            warning "用户名不能为空"
            continue
        fi
        
        if ! user_exists "$username"; then
            warning "用户 $username 不存在"
            read -p "是否重新输入？ (y/n) [默认: y]: " retry
            retry=${retry:-y}
            
            case "$retry" in
                [Nn]*)
                    info "返回主菜单"
                    return 0
                    ;;
            esac
        else
            break
        fi
    done
    
    # 显示用户信息
    echo ""
    info "用户 $username 的信息："
    echo "=========================================="
    id "$username"
    echo "Home目录: $(getent passwd "$username" | cut -d: -f6)"
    echo "=========================================="
    
    # 确认删除
    warning "警告：此操作将删除用户 $username 及其home目录！"
    read -p "确认删除用户 $username？ (y/n) [默认: n]: " confirm
    confirm=${confirm:-n}
    
    case "$confirm" in
        [Yy]*)
            # 询问是否删除home目录
            read -p "是否同时删除用户的home目录？ (y/n) [默认: y]: " delete_home
            delete_home=${delete_home:-y}
            
            case "$delete_home" in
                [Yy]*|"")
                    userdel -r "$username"
                    if [ $? -eq 0 ]; then
                        success "用户 $username 及其home目录已成功删除"
                    else
                        error "删除用户失败"
                    fi
                    ;;
                [Nn]*)
                    userdel "$username"
                    if [ $? -eq 0 ]; then
                        success "用户 $username 已成功删除，home目录保留"
                    else
                        error "删除用户失败"
                    fi
                    ;;
            esac
            ;;
        [Nn]*|"")
            info "取消删除操作"
            ;;
    esac
}

# 修改用户组
modify_user_groups() {
    echo ""
    info "开始修改用户组流程..."
    
    # 获取用户名
    while true; do
        read -p "请输入用户名: " username
        
        if [ -z "$username" ]; then
            warning "用户名不能为空"
            continue
        fi
        
        if ! user_exists "$username"; then
            warning "用户 $username 不存在"
            read -p "是否重新输入？ (y/n) [默认: y]: " retry
            retry=${retry:-y}
            
            case "$retry" in
                [Nn]*)
                    info "返回主菜单"
                    return 0
                    ;;
            esac
        else
            break
        fi
    done
    
    # 显示用户当前所属组
    echo ""
    info "用户 $username 当前所属组："
    echo "=========================================="
    groups "$username"
    echo "=========================================="
    
    # 选择操作类型
    echo ""
    echo "请选择操作类型："
    echo "  1) 将用户添加到组"
    echo "  2) 将用户从组移除"
    echo "  3) 返回主菜单"
    read -p "请输入选择 (1/2/3) [默认: 1]: " operation
    operation=${operation:-1}
    
    case "$operation" in
        1)
            # 将用户添加到组
            while true; do
                read -p "请输入要添加到的组名: " groupname
                
                if [ -z "$groupname" ]; then
                    warning "组名不能为空"
                    continue
                fi
                
                if ! group_exists "$groupname"; then
                    question "组 $groupname 不存在，是否创建？ (y/n) [默认: y]: "
                    read create_group
                    create_group=${create_group:-y}
                    
                    case "$create_group" in
                        [Yy]*|"")
                            groupadd "$groupname"
                            if [ $? -eq 0 ]; then
                                success "组 $groupname 创建成功"
                            else
                                error "组创建失败"
                                continue
                            fi
                            ;;
                        [Nn]*)
                            warning "请重新输入一个已存在的组名"
                            continue
                            ;;
                    esac
                fi
                
                if user_in_group "$username" "$groupname"; then
                    warning "用户 $username 已经在组 $groupname 中"
                else
                    usermod -aG "$groupname" "$username"
                    if [ $? -eq 0 ]; then
                        success "用户 $username 已成功添加到组 $groupname"
                    else
                        error "添加到组失败"
                    fi
                fi
                
                # 询问是否继续修改
                echo ""
                question "是否继续为 $username 修改其他组？ (y/n) [默认: n]: "
                read continue_modify
                continue_modify=${continue_modify:-n}
                
                case "$continue_modify" in
                    [Yy]*)
                        continue
                        ;;
                    [Nn]*|"")
                        break
                        ;;
                esac
            done
            ;;
        2)
            # 将用户从组移除
            while true; do
                read -p "请输入要从哪个组移除: " groupname
                
                if [ -z "$groupname" ]; then
                    warning "组名不能为空"
                    continue
                fi
                
                if ! group_exists "$groupname"; then
                    warning "组 $groupname 不存在"
                    continue
                fi
                
                if ! user_in_group "$username" "$groupname"; then
                    warning "用户 $username 不在组 $groupname 中"
                else
                    # 注意：gpasswd 只能用于从附加组中移除用户
                    # 不能从主组中移除
                    gpasswd -d "$username" "$groupname"
                    if [ $? -eq 0 ]; then
                        success "用户 $username 已成功从组 $groupname 移除"
                    else
                        error "从组移除失败，可能这是用户的主组"
                        warning "如果需要修改主组，请使用 usermod -g 命令"
                    fi
                fi
                
                # 询问是否继续修改
                echo ""
                question "是否继续为 $username 修改其他组？ (y/n) [默认: n]: "
                read continue_modify
                continue_modify=${continue_modify:-n}
                
                case "$continue_modify" in
                    [Yy]*)
                        continue
                        ;;
                    [Nn]*|"")
                        break
                        ;;
                esac
            done
            ;;
        3|*)
            info "返回主菜单"
            return 0
            ;;
    esac
    
    # 显示修改后的组信息
    echo ""
    info "用户 $username 修改后的所属组："
    echo "=========================================="
    groups "$username"
    echo "=========================================="
}

# 显示所有用户
list_users() {
    echo ""
    info "系统用户列表："
    echo "=========================================="
    echo "用户名      UID      GID     描述              Home目录"
    echo "------------------------------------------"
    
    # 获取/etc/passwd中的用户信息，排除系统用户（UID>=1000）
    awk -F: '$3 >= 1000 && $3 < 65534 {printf "%-10s %-8s %-8s %-16s %s\n", $1, $3, $4, $5, $6}' /etc/passwd
    
    echo "=========================================="
    
    # 询问是否显示详细信息
    echo ""
    question "是否显示详细信息（包括组信息）？ (y/n) [默认: n]: "
    read show_details
    show_details=${show_details:-n}
    
    case "$show_details" in
        [Yy]*)
            echo ""
            info "用户详细信息："
            echo "=========================================="
            awk -F: '$3 >= 1000 && $3 < 65534 {
                printf "用户名: %s\n", $1
                printf "  UID: %s, GID: %s\n", $3, $4
                printf "  描述: %s\n", $5
                printf "  Home目录: %s\n", $6
                printf "  默认Shell: %s\n", $7
                system("groups " $1 " 2>/dev/null | cut -d: -f2")
                printf "------------------------------------------\n"
            }' /etc/passwd
            echo "=========================================="
            ;;
    esac
}

# 显示所有组
list_groups() {
    echo ""
    info "系统组列表："
    echo "=========================================="
    echo "组名      GID      成员"
    echo "------------------------------------------"
    
    # 获取/etc/group中的组信息，排除系统组（GID>=1000）
    awk -F: '$3 >= 1000 && $3 < 65534 {
        printf "%-10s %-8s ", $1, $3
        if ($4 == "") {
            printf "无\n"
        } else {
            printf "%s\n", $4
        }
    }' /etc/group
    
    echo "=========================================="
}

# 主函数
main() {
    # 检查root权限
    check_root
    
    while true; do
        show_menu
        
        read -p "请输入选择 (1-6) [默认: 6]: " choice
        choice=${choice:-6}
        
        case "$choice" in
            1)
                add_user
                ;;
            2)
                delete_user
                ;;
            3)
                modify_user_groups
                ;;
            4)
                list_users
                ;;
            5)
                list_groups
                ;;
            6)
                echo ""
                info "感谢使用用户管理系统，再见！"
                exit 0
                ;;
            *)
                warning "无效选择，请重新输入"
                ;;
        esac
        
        # 按任意键继续
        if [ "$choice" != "6" ]; then
            echo ""
            read -p "按 Enter 键继续..."
        fi
    done
}

# 执行主函数
main