#!/bin/bash

# 创建用户脚本：创建具有 sudo 权限和自定义 home 目录的用户，并可靠设置默认密码

# 检查是否以 root 身份运行
if [[ $EUID -ne 0 ]]; then
   echo "错误: 此脚本必须以 root 身份运行！" 
   exit 1
fi

# 显示用法说明
usage() {
    echo "用法: $0 <用户名> <家目录路径>"
    echo "示例: $0 john /home/john"
    exit 1
}

# 检查参数数量
if [ $# -ne 2 ]; then
    usage
fi

USERNAME=$1
HOME_DIR=$2
# 设置你的默认明文密码（注意：#号在shell中有特殊含义，需要小心处理）
DEFAULT_PLAIN_PASS="SG#1644"

# 验证用户名有效性
if ! [[ "$USERNAME" =~ ^[a-z_][a-z0-9_-]*$ ]]; then
    echo "错误: 用户名只能包含小写字母、数字、下划线和连字符，且必须以字母或下划线开头"
    exit 1
fi

# 检查用户是否已存在
if id "$USERNAME" &>/dev/null; then
    echo "错误: 用户 '$USERNAME' 已存在！"
    exit 1
fi

# 检查 home 目录是否已存在
if [ -d "$HOME_DIR" ]; then
    echo "警告: 目录 '$HOME_DIR' 已存在"
    read -p "是否继续？这可能会覆盖现有目录的权限 [y/N]: " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 创建用户（先不设置密码）
echo "正在创建用户: $USERNAME"
useradd -m -d "$HOME_DIR" -s /bin/bash "$USERNAME"

if [ $? -ne 0 ]; then
    echo "错误: 创建用户失败！"
    exit 1
fi

# --- 关键修复部分：使用 chpasswd 可靠设置密码 ---
echo "正在为用户设置默认密码..."

# 方法1：使用 chpasswd（最可靠的方法，推荐）
# 注意：这里使用管道传递密码，密码中的#不会被视为注释
echo "$USERNAME:$DEFAULT_PLAIN_PASS" | chpasswd

if [ $? -eq 0 ]; then
    echo "✓ 密码设置成功（使用chpasswd）"
else
    echo "警告: chpasswd 设置密码失败，尝试备用方法..."
    
    # 方法2：使用 passwd --stdin（如果系统支持）
    if echo "$DEFAULT_PLAIN_PASS" | passwd --stdin "$USERNAME" &>/dev/null; then
        echo "✓ 密码设置成功（使用passwd --stdin）"
    else
        # 方法3：使用交互式expect（作为最后的手段）
        echo "尝试使用expect设置密码..."
        if command -v expect &>/dev/null; then
            expect << EOF
spawn passwd $USERNAME
expect "new password:"
send "$DEFAULT_PLAIN_PASS\r"
expect "retype new password:"
send "$DEFAULT_PLAIN_PASS\r"
expect eof
EOF
            if [ $? -eq 0 ]; then
                echo "✓ 密码设置成功（使用expect）"
            else
                echo "✗ 所有密码设置方法都失败了！"
                echo "请手动运行: sudo passwd $USERNAME"
            fi
        else
            echo "✗ expect命令未安装，请手动设置密码:"
            echo "sudo passwd $USERNAME"
        fi
    fi
fi
# --- 关键修复部分结束 ---

# 添加用户到 sudo 组
echo "正在为用户添加 sudo 权限..."
usermod -aG sudo "$USERNAME"

if [ $? -ne 0 ]; then
    echo "错误: 添加用户到 sudo 组失败！"
    exit 1
fi

# 设置 home 目录权限
chmod 700 "$HOME_DIR"
chown -R "$USERNAME:$USERNAME" "$HOME_DIR"

# 检查SSH相关配置
echo ""
echo "检查SSH相关配置..."

# 确保用户家目录下的.ssh目录权限正确（如果存在）
USER_SSH_DIR="$HOME_DIR/.ssh"
if [ -d "$USER_SSH_DIR" ]; then
    chmod 700 "$USER_SSH_DIR"
    chown -R "$USERNAME:$USERNAME" "$USER_SSH_DIR"
    if [ -f "$USER_SSH_DIR/authorized_keys" ]; then
        chmod 600 "$USER_SSH_DIR/authorized_keys"
    fi
    echo "✓ 已设置.ssh目录权限"
fi

# 检查PAM配置（影响密码登录）
if [ -f /etc/ssh/sshd_config ]; then
    PASSWORD_AUTH=$(grep -i "^PasswordAuthentication" /etc/ssh/sshd_config || echo "PasswordAuthentication yes")
    echo "SSH密码认证设置: $PASSWORD_AUTH"
    
    # 如果配置中PasswordAuthentication是no，提醒用户
    if echo "$PASSWORD_AUTH" | grep -qi "no"; then
        echo "警告: SSH密码认证可能被禁用！"
        echo "如需启用，请修改 /etc/ssh/sshd_config 并设置: PasswordAuthentication yes"
        echo "然后重启SSH服务: sudo systemctl restart ssh"
    fi
fi

# 检查用户账户状态
echo ""
echo "检查用户账户状态..."
# 检查账户是否被锁定
if passwd -S "$USERNAME" | grep -q "L"; then
    echo "警告: 用户账户被锁定！正在解锁..."
    usermod -U "$USERNAME"
    echo "✓ 账户已解锁"
fi

# 强制用户首次登录时修改密码（安全建议）
echo "正在设置密码过期策略..."
passwd --expire "$USERNAME"
echo "✓ 用户首次登录时必须更改密码"

# 显示创建信息
echo ""
echo "==========================================="
echo "用户创建成功！"
echo "用户名: $USERNAME"
echo "家目录: $HOME_DIR"
echo "初始默认密码: $DEFAULT_PLAIN_PASS"
echo "Shell: $(getent passwd "$USERNAME" | cut -d: -f7)"
echo "用户组: $(groups "$USERNAME")"
echo "==========================================="
echo ""
echo "重要提示:"
echo "1. 用户首次登录时必须更改密码"
echo "2. 测试SSH登录: ssh $USERNAME@$(hostname -I | awk '{print $1}')"
echo "3. 测试本地登录: su - $USERNAME"
echo "4. 测试 sudo 权限: sudo -l -U $USERNAME"
echo ""
echo "如果仍然无法SSH登录，请检查:"
echo "1. /etc/ssh/sshd_config 中的 PasswordAuthentication 设置"
echo "2. 用户账户状态: sudo passwd -S $USERNAME"
echo "3. PAM配置: /etc/pam.d/sshd 和 /etc/pam.d/common-auth"