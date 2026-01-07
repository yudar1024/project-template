#!/bin/bash

# 创建用户脚本：创建具有 sudo 权限和自定义 home 目录的用户，并设置默认密码

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
# 设置你的默认明文密码
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

# --- 关键修改部分：生成加密密码并创建用户 ---
echo "正在创建用户: $USERNAME，并设置默认密码"

# 方法1：使用 openssl 生成加密密码 (推荐，更通用)
ENCRYPTED_PASS=$(openssl passwd -6 "$DEFAULT_PLAIN_PASS" 2>/dev/null)

# 如果 openssl 失败，尝试使用更古老的 crypt 函数（仅作后备，不建议）
if [ -z "$ENCRYPTED_PASS" ]; then
    echo "警告: openssl 生成密码失败，尝试使用 Perl 的 crypt 函数。"
    ENCRYPTED_PASS=$(perl -e 'print crypt($ARGV[0], "salt")' "$DEFAULT_PLAIN_PASS")
# fi

# 使用 useradd 创建用户，并直接指定加密后的密码
useradd -m -d "$HOME_DIR" -s /bin/bash -p "$ENCRYPTED_PASS" "$USERNAME"

if [ $? -ne 0 ]; then
    echo "错误: 创建用户失败！"
    exit 1
fi
# --- 关键修改部分结束 ---

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

# 强制用户首次登录时修改密码（强烈建议的安全措施）
# 取消下面一行的注释来启用此功能
# passwd --expire "$USERNAME"

# 显示创建信息
echo "==========================================="
echo "用户创建成功！"
echo "用户名: $USERNAME"
echo "家目录: $HOME_DIR"
echo "初始默认密码: $DEFAULT_PLAIN_PASS"
echo "Shell: $(getent passwd "$USERNAME" | cut -d: -f7)"
echo "用户组: $(groups "$USERNAME")"
echo "==========================================="
echo ""
echo "安全警告：用户首次登录后应立即修改此默认密码！"
echo "测试登录: su - $USERNAME"
echo "测试 sudo 权限: sudo -l -U $USERNAME"
