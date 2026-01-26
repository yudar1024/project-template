#!/bin/bash

# ====================================================
# 系统优化与开发环境一键安装脚本
# 功能：1.检测系统版本并替换为阿里源（自动检测是否已替换）
#       2.安装zsh和ohmyzsh（使用gitee源）
#       3.安装常用开发工具版本管理器
#       4.安装Docker和Docker Compose
# 特点：支持静默/交互模式，自动检测已安装组件
# 作者：Linux系统管理员
# ====================================================

# 注意：暂时禁用 set -e，以便在用户跳过时脚本能继续执行
# set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 安装模式：interactive（交互）或silent（静默）
INSTALL_MODE="interactive"

# 用户选择是否安装的标记
INSTALL_ZSH=false
INSTALL_OMZ=false
INSTALL_PLUGINS=false
INSTALL_FNM=false
INSTALL_G=false
INSTALL_UV=false
INSTALL_DOCKER=false

# 跳过标记
SKIP_ZSH=false
SKIP_OMZ=false
SKIP_PLUGINS=false
SKIP_FNM=false
SKIP_G=false
SKIP_UV=false
SKIP_DOCKER=false

# 错误计数器
ERROR_COUNT=0

# 打印彩色信息
info() { echo -e "${BLUE}[INFO]${NC} $1"; }
success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; ERROR_COUNT=$((ERROR_COUNT + 1)); }
question() { echo -e "${CYAN}[QUESTION]${NC} $1"; }
debug() { echo -e "${PURPLE}[DEBUG]${NC} $1"; }

# 安全执行函数，防止set -e导致退出
safe_execute() {
    local func_name="$1"
    local func_desc="$2"
    
    info "开始执行: $func_desc"
    
    # 执行函数并捕获可能的错误
    if $func_name; then
        success "$func_desc 执行成功"
        return 0
    else
        local exit_code=$?
        if [ $exit_code -eq 100 ]; then
            # 100是用户跳过的特殊代码
            info "用户选择跳过 $func_desc"
        else
            warning "$func_desc 执行失败或跳过 (退出码: $exit_code)"
        fi
        return 0  # 总是返回成功，以便继续执行后续函数
    fi
}

# 检查是否以root运行
check_root() {
    if [[ $EUID -ne 0 ]]; then
        error "请使用root权限运行此脚本"
        echo "使用: sudo bash $0"
        exit 1
    fi
}

# 选择安装模式
select_install_mode() {
    echo ""
    echo "================================================"
    echo "  请选择安装模式："
    echo "  1) 交互模式 (默认) - 每个步骤都会询问是否安装"
    echo "  2) 静默模式 - 自动安装所有组件，无需确认"
    echo "================================================"
    read -p "请输入选择 (1/2) [默认: 1]: " mode_choice
    
    case "$mode_choice" in
        1|"")
            INSTALL_MODE="interactive"
            info "已选择交互模式"
            ;;
        2)
            INSTALL_MODE="silent"
            info "已选择静默模式，将自动安装所有组件"
            ;;
        *)
            warning "无效选择，使用默认交互模式"
            INSTALL_MODE="interactive"
            ;;
    esac
}

# 询问用户是否安装某个组件
ask_to_install() {
    local component_name="$1"
    local component_desc="$2"
    
    if [[ "$INSTALL_MODE" == "silent" ]]; then
        echo -e "${GREEN}[自动安装]${NC} $component_desc"
        return 0  # 静默模式下总是安装
    fi
    
    echo ""
    question "是否安装 $component_name？"
    echo "  $component_desc"
    read -p "请输入 (y/n) [默认: y]: " answer
    
    case "$answer" in
        [Yy]*|"")
            echo -e "${GREEN}[用户选择]${NC} 安装 $component_name"
            return 0
            ;;
        [Nn]*)
            echo -e "${YELLOW}[用户选择]${NC} 跳过 $component_name"
            return 1  # 返回1表示用户选择跳过
            ;;
        *)
            echo -e "${YELLOW}[使用默认]${NC} 安装 $component_name"
            return 0
            ;;
    esac
}

# 检测系统类型和版本
detect_os() {
    info "检测系统类型和版本..."
    
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS_NAME=$ID
        OS_VERSION=$VERSION_ID
        OS_PRETTY_NAME=$PRETTY_NAME
    elif [ -f /etc/redhat-release ]; then
        OS_NAME=$(cat /etc/redhat-release | awk '{print $1}' | tr '[:upper:]' '[:lower:]')
        OS_VERSION=$(cat /etc/redhat-release | grep -oE '[0-9]+\.[0-9]+' | head -1)
        OS_PRETTY_NAME=$(cat /etc/redhat-release)
    else
        error "无法检测操作系统类型"
        exit 1
    fi
    
    info "检测到系统: $OS_PRETTY_NAME"
    
    # 支持的系统列表
    SUPPORTED_UBUNTU_VERSIONS=("20.04" "22.04" "24.04")
    SUPPORTED_ROCKY_VERSIONS=("8" "9")
    
    if [[ "$OS_NAME" == "ubuntu" ]]; then
        if [[ ! " ${SUPPORTED_UBUNTU_VERSIONS[@]} " =~ " ${OS_VERSION} " ]]; then
            warning "Ubuntu $OS_VERSION 可能不完全支持，但脚本将继续运行"
        fi
    elif [[ "$OS_NAME" == "rocky" ]]; then
        if [[ ! " ${SUPPORTED_ROCKY_VERSIONS[@]} " =~ " ${OS_VERSION} " ]]; then
            warning "Rocky Linux $OS_VERSION 可能不完全支持，但脚本将继续运行"
        fi
    else
        error "不支持的操作系统: $OS_NAME"
        error "仅支持 Ubuntu 20.04+/22.04+/24.04+ 和 Rocky Linux 8/9"
        exit 1
    fi
}

# 检测是否已使用阿里源
check_aliyun_source() {
    info "检测是否已使用阿里云镜像源..."
    
    local is_aliyun=false
    
    if [[ "$OS_NAME" == "ubuntu" ]]; then
        if grep -q "mirrors.aliyun.com" /etc/apt/sources.list 2>/dev/null; then
            is_aliyun=true
        fi
        
        # 检查sources.list.d目录
        for file in /etc/apt/sources.list.d/*.list; do
            if [ -f "$file" ] && grep -q "mirrors.aliyun.com" "$file" 2>/dev/null; then
                is_aliyun=true
            fi
        done
        
    elif [[ "$OS_NAME" == "rocky" ]]; then
        if [ -d /etc/yum.repos.d ]; then
            for file in /etc/yum.repos.d/*.repo; do
                if [ -f "$file" ] && grep -q "mirrors.aliyun.com" "$file" 2>/dev/null; then
                    is_aliyun=true
                fi
            done
        fi
    fi
    
    if [ "$is_aliyun" = true ]; then
        success "检测到已使用阿里云镜像源"
        return 0
    else
        warning "未检测到阿里云镜像源"
        return 1
    fi
}

# 备份原有源
backup_sources() {
    info "备份原有软件源..."
    
    local backup_time=$(date +%Y%m%d%H%M%S)
    
    if [[ "$OS_NAME" == "ubuntu" ]]; then
        if [ ! -f "/etc/apt/sources.list.backup.$backup_time" ]; then
            cp /etc/apt/sources.list "/etc/apt/sources.list.backup.$backup_time"
            success "Ubuntu源已备份到 /etc/apt/sources.list.backup.$backup_time"
        fi
    elif [[ "$OS_NAME" == "rocky" ]]; then
        if [ -d /etc/yum.repos.d ] && [ ! -d "/etc/yum.repos.d/backup.$backup_time" ]; then
            mkdir -p "/etc/yum.repos.d/backup.$backup_time"
            cp /etc/yum.repos.d/*.repo "/etc/yum.repos.d/backup.$backup_time/" 2>/dev/null || true
            success "Rocky Linux源已备份到 /etc/yum.repos.d/backup.$backup_time/"
        fi
    fi
}

# 替换为阿里源
replace_with_aliyun_source() {
    # 先检测是否已使用阿里源
    if check_aliyun_source; then
        info "阿里云镜像源已配置，跳过替换"
        return 0
    fi
    
    info "正在替换为阿里云镜像源..."
    
    # 询问是否替换源
    if ! ask_to_install "阿里云镜像源" "将系统软件源替换为阿里云镜像源（加速软件下载）"; then
        warning "用户选择跳过替换软件源"
        return 0  # 返回0而不是1，表示正常跳过
    fi
    
    # 备份原有源
    backup_sources
    
    if [[ "$OS_NAME" == "ubuntu" ]]; then
        # 获取版本代号
        if [ -z "$VERSION_CODENAME" ]; then
            VERSION_CODENAME=$(lsb_release -cs 2>/dev/null || echo "jammy")
        fi
        
        # Ubuntu 替换为阿里源
        cat > /etc/apt/sources.list << EOF
# 阿里云镜像源
deb https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME main restricted universe multiverse
deb https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME-security main restricted universe multiverse
deb https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME-updates main restricted universe multiverse
deb https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME-proposed main restricted universe multiverse
deb https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME-backports main restricted universe multiverse

# 源代码仓库（可选）
# deb-src https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME main restricted universe multiverse
# deb-src https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME-security main restricted universe multiverse
# deb-src https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME-updates main restricted universe multiverse
# deb-src https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME-proposed main restricted universe multiverse
# deb-src https://mirrors.aliyun.com/ubuntu/ $VERSION_CODENAME-backports main restricted universe multiverse
EOF
        success "Ubuntu阿里源配置完成"
        
    elif [[ "$OS_NAME" == "rocky" ]]; then
        # Rocky Linux 替换为阿里源
        if [[ "$OS_VERSION" == "9" ]]; then
            cat > /etc/yum.repos.d/Rocky-Base.repo << EOF
# Rocky Linux 9 - BaseOS - 阿里云
[baseos]
name=Rocky Linux \$releasever - BaseOS - mirrors.aliyun.com
baseurl=https://mirrors.aliyun.com/rockylinux/\$releasever/BaseOS/\$basearch/os/
gpgcheck=1
enabled=1
gpgkey=https://mirrors.aliyun.com/rockylinux/RPM-GPG-KEY-Rocky-9

[appstream]
name=Rocky Linux \$releasever - AppStream - mirrors.aliyun.com
baseurl=https://mirrors.aliyun.com/rockylinux/\$releasever/AppStream/\$basearch/os/
gpgcheck=1
enabled=1
gpgkey=https://mirrors.aliyun.com/rockylinux/RPM-GPG-KEY-Rocky-9
EOF
            cat > /etc/yum.repos.d/Rocky-Extras.repo << EOF
# Rocky Linux 9 - Extras - 阿里云
[extras]
name=Rocky Linux \$releasever - Extras - mirrors.aliyun.com
baseurl=https://mirrors.aliyun.com/rockylinux/\$releasever/extras/\$basearch/os/
gpgcheck=1
enabled=1
gpgkey=https://mirrors.aliyun.com/rockylinux/RPM-GPG-KEY-Rocky-9
EOF
        elif [[ "$OS_VERSION" == "8" ]]; then
            cat > /etc/yum.repos.d/Rocky-Base.repo << EOF
# Rocky Linux 8 - BaseOS - 阿里云
[BaseOS]
name=Rocky Linux \$releasever - BaseOS - mirrors.aliyun.com
baseurl=https://mirrors.aliyun.com/rockylinux/\$releasever/BaseOS/\$basearch/os/
gpgcheck=1
enabled=1
gpgkey=https://mirrors.aliyun.com/rockylinux/RPM-GPG-KEY-Rocky-8

[AppStream]
name=Rocky Linux \$releasever - AppStream - mirrors.aliyun.com
baseurl=https://mirrors.aliyun.com/rockylinux/\$releasever/AppStream/\$basearch/os/
gpgcheck=1
enabled=1
gpgkey=https://mirrors.aliyun.com/rockylinux/RPM-GPG-KEY-Rocky-8
EOF
            cat > /etc/yum.repos.d/Rocky-Extras.repo << EOF
# Rocky Linux 8 - Extras - 阿里云
[extras]
name=Rocky Linux \$releasever - Extras - mirrors.aliyun.com
baseurl=https://mirrors.aliyun.com/rockylinux/\$releasever/extras/\$basearch/os/
gpgcheck=1
enabled=1
gpgkey=https://mirrors.aliyun.com/rockylinux/RPM-GPG-KEY-Rocky-8
EOF
        fi
        success "Rocky Linux阿里源配置完成"
    fi
    
    # 更新包管理器缓存
    update_package_manager
    return 0
}

# 更新系统包管理器
update_package_manager() {
    info "更新系统包管理器缓存..."
    
    if [[ "$OS_NAME" == "ubuntu" ]]; then
        apt-get update -y
        success "Ubuntu包管理器更新完成"
        
    elif [[ "$OS_NAME" == "rocky" ]]; then
        dnf makecache -y
        success "Rocky Linux包管理器更新完成"
    fi
}

# 安装zsh
install_zsh() {
    # 检查是否已跳过
    if [ "$SKIP_ZSH" = true ]; then
        info "用户已选择跳过安装zsh"
        return 0  # 返回0而不是1
    fi
    
    # 检查是否已安装
    if command -v zsh &> /dev/null; then
        warning "zsh已安装，跳过安装步骤"
        INSTALL_ZSH=true
        return 0
    fi
    
    # 询问是否安装
    if ! ask_to_install "zsh" "更强大的shell替代bash，支持丰富插件和主题"; then
        warning "用户选择跳过安装zsh"
        SKIP_ZSH=true
        return 0  # 返回0而不是1，表示正常跳过
    fi
    
    INSTALL_ZSH=true
    info "安装zsh..."
    
    if [[ "$OS_NAME" == "ubuntu" ]]; then
        apt-get install -y zsh curl wget git
    elif [[ "$OS_NAME" == "rocky" ]]; then
        dnf install -y zsh curl wget git
    fi
    
    # 检查zsh是否安装成功
    if command -v zsh &> /dev/null; then
        success "zsh安装完成"
        
        # 询问是否设置为默认shell
        if [[ "$INSTALL_MODE" == "silent" ]] || ask_to_install "设置zsh为默认shell" "将当前用户的默认shell设置为zsh"; then
            # 设置zsh为默认shell（仅对当前用户）
            if [ -n "$SUDO_USER" ]; then
                current_shell=$(getent passwd $SUDO_USER | cut -d: -f7)
                if [[ "$current_shell" != "$(which zsh)" ]]; then
                    chsh -s $(which zsh) $SUDO_USER
                    success "已将 $SUDO_USER 的默认shell设置为zsh"
                else
                    info "$SUDO_USER 的默认shell已经是zsh"
                fi
            else
                current_shell=$(getent passwd $(whoami) | cut -d: -f7)
                if [[ "$current_shell" != "$(which zsh)" ]]; then
                    chsh -s $(which zsh)
                    success "已将root的默认shell设置为zsh"
                else
                    info "root的默认shell已经是zsh"
                fi
            fi
        else
            warning "未设置zsh为默认shell"
        fi
    else
        error "zsh安装失败"
        return 1  # 真正的错误返回1
    fi
}

# 使用gitee源安装ohmyzsh
install_ohmyzsh() {
    # 检查是否已跳过
    if [ "$SKIP_OMZ" = true ]; then
        info "用户已选择跳过安装ohmyzsh"
        return 0  # 返回0而不是1
    fi
    
    # 先检查zsh是否安装
    if ! command -v zsh &> /dev/null && [ "$INSTALL_ZSH" = false ]; then
        warning "zsh未安装，跳过ohmyzsh安装"
        SKIP_OMZ=true
        return 0  # 返回0而不是1
    fi
    
    # 检查是否已安装
    if [ -n "$SUDO_USER" ]; then
        USER_HOME=$(eval echo "~$SUDO_USER")
    else
        USER_HOME=$HOME
    fi
    
    if [ -d "$USER_HOME/.oh-my-zsh" ]; then
        warning "ohmyzsh已安装，跳过安装步骤"
        INSTALL_OMZ=true
        return 0
    fi
    
    # 询问是否安装
    if ! ask_to_install "ohmyzsh" "zsh配置框架，提供丰富主题和插件管理"; then
        warning "用户选择跳过安装ohmyzsh"
        SKIP_OMZ=true
        return 0  # 返回0而不是1
    fi
    
    INSTALL_OMZ=true
    info "使用gitee源安装ohmyzsh..."
    
    # 获取当前用户名（如果通过sudo运行，获取原用户名）
    if [ -n "$SUDO_USER" ]; then
        USERNAME=$SUDO_USER
        USER_HOME=$(eval echo "~$SUDO_USER")
    else
        USERNAME=$(whoami)
        USER_HOME=$HOME
    fi
    
    info "将为用户 $USERNAME 安装ohmyzsh"
    
    # 安装ohmyzsh（使用gitee源）
    export RUNZSH=no
    export CHSH=no
    
    info "正在从gitee源下载ohmyzsh..."
    
    # 使用gitee源安装ohmyzsh
    #sh -c "$(curl -fsSL https://gitee.com/mirrors/oh-my-zsh/raw/master/tools/install.sh)" "" --unattended
    sh -c "$(curl -fsSL https://install.ohmyz.sh/)" "" --unattended
    # 如果是通过sudo运行，需要修复权限
    if [ -n "$SUDO_USER" ]; then
        chown -R $SUDO_USER:$SUDO_USER "$USER_HOME/.oh-my-zsh"
        chown -R $SUDO_USER:$SUDO_USER "$USER_HOME/.zshrc"
    fi
    
    # 检查是否安装成功
    if [ -d "$USER_HOME/.oh-my-zsh" ]; then
        success "ohmyzsh安装完成"
    else
        error "ohmyzsh安装失败，尝试备用安装方法..."
        
        # 备用安装方法
        info "尝试备用安装方法..."
        git clone https://gitee.com/mirrors/oh-my-zsh.git "$USER_HOME/.oh-my-zsh"
        if [ -n "$SUDO_USER" ]; then
            chown -R $SUDO_USER:$SUDO_USER "$USER_HOME/.oh-my-zsh"
        fi
        
        if [ -d "$USER_HOME/.oh-my-zsh" ]; then
            cp "$USER_HOME/.oh-my-zsh/templates/zshrc.zsh-template" "$USER_HOME/.zshrc"
            if [ -n "$SUDO_USER" ]; then
                chown $SUDO_USER:$SUDO_USER "$USER_HOME/.zshrc"
            fi
            success "ohmyzsh安装完成（备用方法）"
        else
            error "ohmyzsh安装失败，请手动安装"
            return 1  # 真正的错误返回1
        fi
    fi
}

# 检测ohmyzsh插件是否已安装
check_zsh_plugin_installed() {
    local plugin_name="$1"
    
    if [ -n "$SUDO_USER" ]; then
        USER_HOME=$(eval echo "~$SUDO_USER")
    else
        USER_HOME=$HOME
    fi
    
    local plugin_path="${USER_HOME}/.oh-my-zsh/custom/plugins/${plugin_name}"
    
    if [ -d "$plugin_path" ]; then
        return 0  # 已安装
    else
        return 1  # 未安装
    fi
}

# 安装ohmyzsh常用插件
install_zsh_plugins() {
    # 检查是否已跳过
    if [ "$SKIP_PLUGINS" = true ]; then
        info "用户已选择跳过安装zsh插件"
        return 0  # 返回0而不是1
    fi
    
    # 检查ohmyzsh是否安装
    if [ -n "$SUDO_USER" ]; then
        USER_HOME=$(eval echo "~$SUDO_USER")
    else
        USER_HOME=$HOME
    fi
    
    if [ ! -d "$USER_HOME/.oh-my-zsh" ] && [ "$INSTALL_OMZ" = false ]; then
        warning "ohmyzsh未安装，跳过插件安装"
        SKIP_PLUGINS=true
        return 0  # 返回0而不是1
    fi
    
    # 询问是否安装
    if ! ask_to_install "zsh插件" "安装常用zsh插件：自动建议、语法高亮、自动跳转"; then
        warning "用户选择跳过安装zsh插件"
        SKIP_PLUGINS=true
        return 0  # 返回0而不是1
    fi
    
    INSTALL_PLUGINS=true
    info "安装ohmyzsh常用插件..."
    
    ZSH_CUSTOM="${USER_HOME}/.oh-my-zsh/custom"
    
    # 插件列表：名称、描述、Gitee仓库地址
    local plugins=(
        "zsh-autosuggestions:zsh自动建议插件:https://gitee.com/mirrors/zsh-autosuggestions.git"
        "zsh-syntax-highlighting:zsh语法高亮插件:https://gitee.com/mirrors/zsh-syntax-highlighting.git"
    )
    
    for plugin_info in "${plugins[@]}"; do
        IFS=':' read -r plugin_name plugin_desc plugin_url <<< "$plugin_info"
        
        # 检查是否已安装
        if check_zsh_plugin_installed "$plugin_name"; then
            info "$plugin_name 已安装，跳过"
            continue
        fi
        
        info "安装 $plugin_name - $plugin_desc"
        
        # 安装插件
        git clone "$plugin_url" "${ZSH_CUSTOM}/plugins/${plugin_name}" 2>/dev/null || {
            warning "安装 $plugin_name 失败，尝试备用地址..."
            # 尝试备用地址
            if [[ "$plugin_name" == "zsh-autosuggestions" ]]; then
                git clone "https://github.com/zsh-users/zsh-autosuggestions.git" "${ZSH_CUSTOM}/plugins/${plugin_name}" || {
                    error "无法安装 $plugin_name，跳过此插件"
                    continue
                }
            elif [[ "$plugin_name" == "zsh-syntax-highlighting" ]]; then
                git clone "https://github.com/zsh-users/zsh-syntax-highlighting.git" "${ZSH_CUSTOM}/plugins/${plugin_name}" || {
                    error "无法安装 $plugin_name，跳过此插件"
                    continue
                }
            fi
        }
        
        # 修复权限
        if [ -n "$SUDO_USER" ]; then
            chown -R $SUDO_USER:$SUDO_USER "${ZSH_CUSTOM}/plugins/${plugin_name}"
        fi
        
        success "$plugin_name 安装完成"
    done
    
    # 安装autojump
    info "检查autojump..."
    if ! command -v autojump &> /dev/null; then
        if ask_to_install "autojump" "目录快速跳转工具，输入部分路径名即可跳转"; then
            if [[ "$OS_NAME" == "ubuntu" ]]; then
                apt-get install -y autojump
            elif [[ "$OS_NAME" == "rocky" ]]; then
                dnf install -y autojump
            fi
            success "autojump安装完成"
        else
            warning "跳过autojump安装"
        fi
    else
        info "autojump已安装"
    fi
    
    # 配置zshrc
    info "配置.zshrc..."
    
    # 检查.zshrc是否存在
    if [ ! -f "$USER_HOME/.zshrc" ]; then
        warning ".zshrc文件不存在，创建新配置"
        # 使用ohmyzsh模板
        if [ -f "$USER_HOME/.oh-my-zsh/templates/zshrc.zsh-template" ]; then
            cp "$USER_HOME/.oh-my-zsh/templates/zshrc.zsh-template" "$USER_HOME/.zshrc"
        fi
    fi
    
    # 备份原有zshrc
    if [ -f "$USER_HOME/.zshrc" ]; then
        cp "$USER_HOME/.zshrc" "$USER_HOME/.zshrc.backup.$(date +%Y%m%d%H%M%S)"
    fi
    
    # 创建基本的zsh配置
    cat > "$USER_HOME/.zshrc" << 'EOF'
# 如果从bash环境继承，清理PATH
if [[ -n $BASHRC_LOADED ]]; then
    unset PATH
    export PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
fi

# 设置oh-my-zsh路径
export ZSH="$HOME/.oh-my-zsh"

# 设置主题
ZSH_THEME="agnoster"

# 插件设置
plugins=(
    git
EOF

    # 添加已安装的插件
    if check_zsh_plugin_installed "zsh-autosuggestions"; then
        echo "    zsh-autosuggestions" >> "$USER_HOME/.zshrc"
    fi
    
    if check_zsh_plugin_installed "zsh-syntax-highlighting"; then
        echo "    zsh-syntax-highlighting" >> "$USER_HOME/.zshrc"
    fi
    
    if command -v autojump &> /dev/null; then
        echo "    autojump" >> "$USER_HOME/.zshrc"
    fi

    cat >> "$USER_HOME/.zshrc" << 'EOF'
)

# 加载oh-my-zsh
source $ZSH/oh-my-zsh.sh

# 用户自定义配置

# 设置语言环境
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

# 设置时区
export TZ=Asia/Shanghai

# 历史记录设置
export HISTSIZE=10000
export SAVEHIST=10000
export HISTFILE=~/.zsh_history
setopt HIST_IGNORE_ALL_DUPS
setopt HIST_FIND_NO_DUPS

# 自动补全设置
autoload -U compinit && compinit

# 别名设置
alias ll='ls -alF'
alias la='ls -A'
alias l='ls -CF'
alias zshconfig="vim ~/.zshrc"
alias ohmyzsh="vim ~/.oh-my-zsh"

# 自定义路径
export PATH="$HOME/.local/bin:$PATH"

# 设置代理（如果需要，取消注释）
# export http_proxy=http://127.0.0.1:7890
# export https_proxy=http://127.0.0.1:7890
EOF
    
    # 修复权限
    if [ -n "$SUDO_USER" ]; then
        chown $SUDO_USER:$SUDO_USER "$USER_HOME/.zshrc"
    fi
    
    success "zsh插件安装完成"
}

# 安装fnm (Node版本管理工具)
install_fnm() {
    # 检查是否已跳过
    if [ "$SKIP_FNM" = true ]; then
        info "用户已选择跳过安装fnm"
        return 0  # 返回0而不是1
    fi
    
    # 询问是否安装
    if ! ask_to_install "fnm" "Fast Node Manager - 快速的Node.js版本管理工具"; then
        warning "用户选择跳过安装fnm"
        SKIP_FNM=true
        return 0  # 返回0而不是1
    fi
    
    INSTALL_FNM=true
    info "安装fnm (Fast Node Manager)..."
    
    if [ -n "$SUDO_USER" ]; then
        USER_HOME=$(eval echo "~$SUDO_USER")
    else
        USER_HOME=$HOME
    fi
    
    # 检查是否已安装
    if command -v fnm &> /dev/null; then
        warning "fnm已安装，跳过"
        return 0
    fi
    
    # 安装fnm
    curl -fsSL https://fnm.vercel.app/install | bash
    
    # 配置fnm环境
    cat >> "$USER_HOME/.zshrc" << 'EOF'

# fnm配置
export PATH="$HOME/.fnm:$PATH"
eval "$(fnm env --use-on-cd --shell zsh)"
EOF
    
    # 修复权限
    if [ -n "$SUDO_USER" ]; then
        chown $SUDO_USER:$SUDO_USER "$USER_HOME/.zshrc"
    fi
    
    success "fnm安装完成"
    
    # 询问是否安装Node.js
    if [[ "$INSTALL_MODE" == "silent" ]] || ask_to_install "Node.js LTS" "使用fnm安装Node.js LTS版本"; then
        info "正在安装Node.js LTS..."
        # 切换到用户环境安装
        if [ -n "$SUDO_USER" ]; then
            sudo -u $SUDO_USER bash -c 'source ~/.zshrc 2>/dev/null || true; fnm install --lts && fnm use --lts'
        else
            source ~/.zshrc 2>/dev/null || true
            fnm install --lts
            fnm use --lts
        fi
        success "Node.js LTS安装完成"
    fi
}

# 安装g (Golang版本管理工具)
install_g() {
    # 检查是否已跳过
    if [ "$SKIP_G" = true ]; then
        info "用户已选择跳过安装g"
        return 0  # 返回0而不是1
    fi
    
    # 询问是否安装
    if ! ask_to_install "g" "Go版本管理工具 - 简单的Go语言版本管理"; then
        warning "用户选择跳过安装g"
        SKIP_G=true
        return 0  # 返回0而不是1
    fi
    
    INSTALL_G=true
    info "安装g (Go版本管理工具)..."
    
    if [ -n "$SUDO_USER" ]; then
        USER_HOME=$(eval echo "~$SUDO_USER")
    else
        USER_HOME=$HOME
    fi
    
    # 检查是否已安装
    if command -v g &> /dev/null; then
        warning "g已安装，跳过"
        return 0
    fi
    
    # 安装g
    curl -sSL https://raw.githubusercontent.com/voidint/g/master/install.sh | bash
    
    # 配置g环境
    cat >> "$USER_HOME/.zshrc" << 'EOF'

# g配置 (Go版本管理)
export GOROOT="$HOME/.g/go"
export PATH="$HOME/.g/go/bin:$PATH"
export G_MIRROR=https://golang.google.cn/dl/
EOF
    
    # 修复权限
    if [ -n "$SUDO_USER" ]; then
        chown $SUDO_USER:$SUDO_USER "$USER_HOME/.zshrc"
    fi
    
    success "g安装完成"
    
    # 询问是否安装Go
    if [[ "$INSTALL_MODE" == "silent" ]] || ask_to_install "Go" "使用g安装最新稳定版Go"; then
        info "正在安装Go..."
        # 切换到用户环境安装
        if [ -n "$SUDO_USER" ]; then
            sudo -u $SUDO_USER bash -c 'source ~/.zshrc 2>/dev/null || true; g install latest'
        else
            source ~/.zshrc 2>/dev/null || true
            g install latest
        fi
        success "Go安装完成"
    fi
}

# 安装uv (Python版本管理工具)
install_uv() {
    # 检查是否已跳过
    if [ "$SKIP_UV" = true ]; then
        info "用户已选择跳过安装uv"
        return 0  # 返回0而不是1
    fi
    
    # 询问是否安装
    if ! ask_to_install "uv" "Python版本和包管理工具 - 极速的Python工具链"; then
        warning "用户选择跳过安装uv"
        SKIP_UV=true
        return 0  # 返回0而不是1
    fi
    
    INSTALL_UV=true
    info "安装uv (Python版本管理工具)..."
    
    if [ -n "$SUDO_USER" ]; then
        USER_HOME=$(eval echo "~$SUDO_USER")
    else
        USER_HOME=$HOME
    fi
    
    # 检查是否已安装
    if command -v uv &> /dev/null; then
        warning "uv已安装，跳过"
        return 0
    fi
    
    # 安装uv
    curl -LsSf https://astral.sh/uv/install.sh | sh
    
    # 配置uv环境
    cat >> "$USER_HOME/.zshrc" << 'EOF'

# uv配置 (Python版本管理)
export PATH="$HOME/.local/bin:$PATH"
alias python=python3
alias pip="uv pip"
EOF
    
    # 修复权限
    if [ -n "$SUDO_USER" ]; then
        chown $SUDO_USER:$SUDO_USER "$USER_HOME/.zshrc"
    fi
    
    success "uv安装完成"
    
    # 询问是否安装Python
    if [[ "$INSTALL_MODE" == "silent" ]] || ask_to_install "Python" "使用uv安装Python 3.11"; then
        info "正在安装Python 3.11..."
        # 切换到用户环境安装
        if [ -n "$SUDO_USER" ]; then
            sudo -u $SUDO_USER bash -c 'source ~/.zshrc 2>/dev/null || true; uv python install 3.11'
        else
            source ~/.zshrc 2>/dev/null || true
            uv python install 3.11
        fi
        success "Python 3.11安装完成"
    fi
}

# 安装Docker
install_docker() {
    # 检查是否已跳过
    if [ "$SKIP_DOCKER" = true ]; then
        info "用户已选择跳过安装Docker"
        return 0  # 返回0而不是1
    fi
    
    # 询问是否安装
    if ! ask_to_install "Docker" "容器化平台，用于构建、发布和运行应用程序"; then
        warning "用户选择跳过安装Docker"
        SKIP_DOCKER=true
        return 0  # 返回0而不是1
    fi
    
    INSTALL_DOCKER=true
    info "安装Docker和Docker Compose..."
    
    # 检查是否已安装
    if command -v docker &> /dev/null; then
        warning "Docker已安装，跳过"
    else
        # 卸载旧版本
        if [[ "$OS_NAME" == "ubuntu" ]]; then
            apt-get remove -y docker docker-engine docker.io containerd runc 2>/dev/null || true
        elif [[ "$OS_NAME" == "rocky" ]]; then
            dnf remove -y docker docker-client docker-client-latest docker-common docker-latest docker-latest-logrotate docker-logrotate docker-engine 2>/dev/null || true
        fi
        
        # 安装依赖
        if [[ "$OS_NAME" == "ubuntu" ]]; then
            apt-get install -y \
                ca-certificates \
                curl \
                gnupg \
                lsb-release
        elif [[ "$OS_NAME" == "rocky" ]]; then
            dnf install -y yum-utils
        fi
        
        # 添加Docker官方GPG密钥（使用阿里云镜像）
        mkdir -p /etc/apt/keyrings 2>/dev/null || mkdir -p /etc/pki/rpm-gpg 2>/dev/null
        
        if [[ "$OS_NAME" == "ubuntu" ]]; then
            # Ubuntu使用阿里云镜像
            curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | \
                gpg --dearmor -o /etc/apt/keyrings/docker.gpg
            
            # 设置Docker阿里云镜像源
            echo \
              "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://mirrors.aliyun.com/docker-ce/linux/ubuntu \
              $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
            
            apt-get update -y
            apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
            
        elif [[ "$OS_NAME" == "rocky" ]]; then
            # Rocky Linux使用阿里云镜像
            yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
            
            # 安装Docker
            dnf install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
            
            # 启动Docker服务
            systemctl enable docker
            systemctl start docker
        fi
        
        success "Docker安装完成"
    fi
    
    # 安装Docker Compose独立版本（如果需要）
    info "安装Docker Compose..."
    if ! command -v docker-compose &> /dev/null; then
        # 下载Docker Compose（使用国内镜像）
        DOCKER_COMPOSE_VERSION="v2.24.5"
        curl -L "https://ghproxy.com/https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" \
            -o /usr/local/bin/docker-compose
        
        chmod +x /usr/local/bin/docker-compose
        
        # 创建软链接
        ln -sf /usr/local/bin/docker-compose /usr/bin/docker-compose 2>/dev/null || true
        
        success "Docker Compose安装完成"
    else
        warning "Docker Compose已安装，跳过"
    fi
    
    # 配置Docker镜像加速器（阿里云镜像）
    info "配置Docker镜像加速器..."
    
    mkdir -p /etc/docker
    
    # 如果有阿里云容器镜像服务加速地址，可以替换下面的加速器地址
    cat > /etc/docker/daemon.json << EOF
{
    "registry-mirrors": [
        "https://registry.docker-cn.com",
        "https://nrbewqda.mirror.aliyuncs.com",
        "https://dmmxhzvq.mirror.aliyuncs.com",
        "https://docker.211678.top",
        "https://docker.1panel.live",
        "https://hub.rat.dev",
        "https://docker.m.daocloud.io",
        "https://do.nark.eu.org",
        "https://dockerpull.com",
        "https://dockerproxy.cn",
        "https://docker.awsl9527.cn/",
        "https://hub.domys.cc",
        "https://docker.domys.cc"
    ],
    "exec-opts": ["native.cgroupdriver=systemd"],
    "log-driver": "json-file",
    "log-opts": {
    "max-size": "100m"
    },
    "storage-driver": "overlay2"
}
EOF
    
    # 重启Docker服务
    if systemctl is-active --quiet docker; then
        systemctl daemon-reload
        systemctl restart docker
    fi
    
    # 将当前用户添加到docker组（避免每次使用sudo）
    if [ -n "$SUDO_USER" ]; then
        if ! getent group docker | grep -q "\b$SUDO_USER\b"; then
            usermod -aG docker $SUDO_USER
            info "已将用户 $SUDO_USER 添加到docker组"
            warning "需要重新登录或重启才能使docker组权限生效"
        fi
    fi
    
    success "Docker配置完成"
}

# 显示安装摘要
show_summary() {
    echo ""
    echo "================================================"
    if [ $ERROR_COUNT -eq 0 ]; then
        success "           安装完成！"
    else
        warning "           安装完成，但有 $ERROR_COUNT 个错误"
    fi
    echo "================================================"
    info "安装模式: $INSTALL_MODE"
    echo ""
    info "安装状态："
    
    # 检查阿里源
    if check_aliyun_source; then
        echo "  ✓ 阿里云镜像源 (已配置)"
    else
        echo "  ✗ 阿里云镜像源 (未配置或跳过)"
    fi
    
    # 检查zsh
    if command -v zsh &> /dev/null; then
        echo "  ✓ zsh (已安装)"
    elif [ "$SKIP_ZSH" = true ]; then
        echo "  ✗ zsh (用户选择跳过)"
    else
        echo "  ✗ zsh (未安装)"
    fi
    
    # 检查ohmyzsh
    if [ -n "$SUDO_USER" ]; then
        USER_HOME=$(eval echo "~$SUDO_USER")
    else
        USER_HOME=$HOME
    fi
    
    if [ -d "$USER_HOME/.oh-my-zsh" ]; then
        echo "  ✓ ohmyzsh (已安装)"
    elif [ "$SKIP_OMZ" = true ]; then
        echo "  ✗ ohmyzsh (用户选择跳过)"
    else
        echo "  ✗ ohmyzsh (未安装)"
    fi
    
    # 检查插件
    if check_zsh_plugin_installed "zsh-autosuggestions" || check_zsh_plugin_installed "zsh-syntax-highlighting"; then
        echo "  ✓ zsh常用插件 (已安装)"
    elif [ "$SKIP_PLUGINS" = true ]; then
        echo "  ✗ zsh常用插件 (用户选择跳过)"
    else
        echo "  ✗ zsh常用插件 (未安装)"
    fi
    
    # 检查fnm
    if command -v fnm &> /dev/null; then
        echo "  ✓ fnm (Node.js版本管理) (已安装)"
    elif [ "$SKIP_FNM" = true ]; then
        echo "  ✗ fnm (Node.js版本管理) (用户选择跳过)"
    else
        echo "  ✗ fnm (Node.js版本管理) (未安装)"
    fi
    
    # 检查g
    if command -v g &> /dev/null; then
        echo "  ✓ g (Go版本管理) (已安装)"
    elif [ "$SKIP_G" = true ]; then
        echo "  ✗ g (Go版本管理) (用户选择跳过)"
    else
        echo "  ✗ g (Go版本管理) (未安装)"
    fi
    
    # 检查uv
    if command -v uv &> /dev/null; then
        echo "  ✓ uv (Python版本管理) (已安装)"
    elif [ "$SKIP_UV" = true ]; then
        echo "  ✗ uv (Python版本管理) (用户选择跳过)"
    else
        echo "  ✗ uv (Python版本管理) (未安装)"
    fi
    
    # 检查Docker
    if command -v docker &> /dev/null; then
        echo "  ✓ Docker & Docker Compose (已安装)"
    elif [ "$SKIP_DOCKER" = true ]; then
        echo "  ✗ Docker & Docker Compose (用户选择跳过)"
    else
        echo "  ✗ Docker & Docker Compose (未安装)"
    fi
    
    echo ""
    info "后续操作："
    echo "  1. 重新登录或重启终端以使zsh生效"
    
    if [ -n "$SUDO_USER" ] && command -v docker &> /dev/null; then
        echo "  2. 用户 $SUDO_USER 已添加到docker组"
        echo "     需要重新登录或执行: newgrp docker"
    fi
    
    echo "  3. 常用命令："
    if command -v fnm &> /dev/null; then
        echo "     - fnm install <version>    # 安装Node.js"
    fi
    if command -v g &> /dev/null; then
        echo "     - g install <version>      # 安装Go"
    fi
    if command -v uv &> /dev/null; then
        echo "     - uv python install <version> # 安装Python"
    fi
    if command -v docker &> /dev/null; then
        echo "     - docker --version         # 检查Docker版本"
    fi
    echo ""
    info "配置文件位置："
    echo "  - ohmyzsh配置: ~/.zshrc"
    if command -v docker &> /dev/null; then
        echo "  - Docker配置: /etc/docker/daemon.json"
    fi
    echo "  - 源备份文件: /etc/apt/sources.list.backup.* 或 /etc/yum.repos.d/backup.*"
    echo ""
    warning "注意事项："
    echo "  - 部分配置需要重新登录或重启终端才能生效"
    echo "  - 如需使用代理，请编辑 ~/.zshrc 取消相关注释"
    echo "================================================"
}

# 显示安装计划
show_installation_plan() {
    echo ""
    echo "================================================"
    info "           安装计划"
    echo "================================================"
    echo "安装模式: $INSTALL_MODE"
    echo ""
    echo "将安装以下组件："
    echo "  1. 阿里云镜像源 (如果未配置)"
    echo "  2. zsh - 强大的shell"
    echo "  3. ohmyzsh - zsh配置框架 (使用gitee源)"
    echo "  4. zsh插件 - 自动建议、语法高亮等"
    echo "  5. fnm - Node.js版本管理器"
    echo "  6. g - Go版本管理器"
    echo "  7. uv - Python版本管理器"
    echo "  8. Docker & Docker Compose"
    echo ""
    
    if [[ "$INSTALL_MODE" == "interactive" ]]; then
        info "交互模式：每个组件安装前都会询问是否安装"
        info "         您可以跳过任意组件，脚本会继续询问下一个"
        read -p "按 Enter 键开始安装，或按 Ctrl+C 取消..."
    else
        info "静默模式：将自动安装所有组件"
        read -p "按 Enter 键开始安装，或按 Ctrl+C 取消..."
    fi
}

# 主函数
main() {
    clear
    echo "================================================"
    echo "  系统优化与开发环境一键安装脚本"
    echo "================================================"
    
    # 检查root权限
    check_root
    
    # 选择安装模式
    select_install_mode
    
    # 检测系统
    detect_os
    
    # 显示安装计划
    show_installation_plan
    
    # 替换为阿里源（自动检测是否已配置）
    replace_with_aliyun_source
    
    # 安装zsh
    install_zsh
    
    # 安装ohmyzsh
    install_ohmyzsh
    
    # 安装zsh插件
    install_zsh_plugins
    
    # 安装fnm
    install_fnm
    
    # 安装g
    install_g
    
    # 安装uv
    install_uv
    
    # 安装Docker和Docker Compose
    install_docker
    
    # 显示安装摘要
    show_summary
}

# 执行主函数
main