#!/bin/bash

# 删除用户脚本：安全删除用户账户及其家目录

# 检查是否以 root 身份运行
if [[ $EUID -ne 0 ]]; then
    echo "错误: 此脚本必须以 root 身份运行！" 
    exit 1
fi

# 显示用法说明
usage() {
    echo "用法: $0 <用户名>"
    echo "示例: $0 john"
    exit 1
}

# 检查是否提供了用户名参数
if [ $# -ne 1 ]; then
    usage
fi

USERNAME=$1

# 检查用户是否存在
if ! id "$USERNAME" &>/dev/null; then
    echo "错误: 用户 '$USERNAME' 不存在！"
    exit 1
fi

# 显示用户信息
echo "==========================================="
echo "用户信息:"
echo "用户名: $USERNAME"
echo "用户ID (UID): $(id -u "$USERNAME")"
echo "组ID (GID): $(id -g "$USERNAME")"
echo "用户组: $(groups "$USERNAME")"

# 获取用户的 home 目录（来自 /etc/passwd）
USER_HOME=$(getent passwd "$USERNAME" | cut -d: -f6)
echo "家目录: $USER_HOME"
echo "==========================================="

# 检查用户是否正在运行进程
RUNNING_PROCS=$(ps -u "$USERNAME" -o pid= 2>/dev/null | wc -l)
if [ "$RUNNING_PROCS" -gt 0 ]; then
    echo "警告: 用户 '$USERNAME' 有 $RUNNING_PROCS 个正在运行的进程。"
    read -p "是否要强制终止这些进程？ [y/N]: " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "正在终止用户 '$USERNAME' 的所有进程..."
        pkill -9 -u "$USERNAME"
        sleep 2
    else
        echo "请先手动终止这些进程后再运行此脚本。"
        echo "可以使用以下命令: sudo pkill -9 -u $USERNAME"
        exit 1
    fi
fi

# 询问是否删除 home 目录
DELETE_HOME="n"
if [ -d "$USER_HOME" ]; then
    echo ""
    echo "警告: 即将删除用户 '$USERNAME' 的家目录 '$USER_HOME'"
    echo "目录内容:"
    ls -la "$USER_HOME" 2>/dev/null | head -20
    echo ""
    
    read -p "是否删除家目录及其所有内容？ [y/N]: " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        DELETE_HOME="y"
    else
        echo "将保留家目录 '$USER_HOME'。"
    fi
else
    echo "注意: 用户 '$USERNAME' 的家目录 '$USER_HOME' 不存在。"
fi

# 最终确认
echo ""
echo "====== 最终确认 ======"
echo "即将执行的操作:"
echo "1. 删除用户 '$USERNAME'"
if [ "$DELETE_HOME" = "y" ]; then
    echo "2. 删除家目录 '$USER_HOME' 及其所有内容"
else
    echo "2. 保留家目录 '$USER_HOME'"
fi
echo "======================"

read -p "确认执行以上操作？ [y/N]: " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "操作已取消。"
    exit 0
fi

# 执行删除操作
echo "正在删除用户 '$USERNAME'..."

if [ "$DELETE_HOME" = "y" ]; then
    # 删除用户及其 home 目录
    userdel -r "$USERNAME"
    RESULT=$?
    
    if [ $RESULT -eq 0 ]; then
        # 再次检查目录是否真的被删除
        if [ -d "$USER_HOME" ]; then
            echo "警告: userdel 命令可能未完全删除家目录。"
            read -p "是否要强制删除目录 '$USER_HOME'？ [y/N]: " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                rm -rf "$USER_HOME"
                echo "已强制删除目录 '$USER_HOME'。"
            fi
        else
            echo "用户 '$USERNAME' 及其家目录已成功删除。"
        fi
    else
        echo "错误: 删除用户失败！错误代码: $RESULT"
        exit 1
    fi
else
    # 只删除用户，保留 home 目录
    userdel "$USERNAME"
    RESULT=$?
    
    if [ $RESULT -eq 0 ]; then
        # 更改 home 目录的所有权（从用户变为 root，避免成为无主文件）
        if [ -d "$USER_HOME" ]; then
            echo "正在更改家目录 '$USER_HOME' 的所有权..."
            chown -R root:root "$USER_HOME"
            echo "家目录 '$USER_HOME' 的所有权已更改为 root。"
        fi
        
        echo "用户 '$USERNAME' 已删除，家目录已保留。"
    else
        echo "错误: 删除用户失败！错误代码: $RESULT"
        exit 1
    fi
fi

# 清理邮件池（如果有的话）
if [ -d "/var/mail/$USERNAME" ] || [ -f "/var/mail/$USERNAME" ]; then
    read -p "是否删除用户的邮件池 (/var/mail/$USERNAME)？ [y/N]: " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        rm -f "/var/mail/$USERNAME"
        echo "用户邮件池已删除。"
    fi
fi

echo "操作完成！"