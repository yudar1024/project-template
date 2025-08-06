package config

import (
	"github.com/spf13/viper"
)

// Config 结构体定义了应用的所有配置项
// 使用 mapstructure 标签来让 viper 自动 unmarshal 配置

type Config struct {
	Server   ServerConfig   `mapstructure:"server"`
	Database DatabaseConfig `mapstructure:"database"`
	Log      LogConfig      `mapstructure:"log"`
	JWT      JWTConfig      `mapstructure:"jwt"`
}

// ServerConfig 定义了服务器相关的配置
type ServerConfig struct {
	Address string `mapstructure:"address"`
}

// DatabaseConfig 定义了数据库相关的配置
type DatabaseConfig struct {
	DSN string `mapstructure:"dsn"`
}

// LogConfig 定义了日志相关的配置
type LogConfig struct {
	Level string `mapstructure:"level"`
	Path  string `mapstructure:"path"`
}

// JWTConfig 定义了 JWT 相关的配置
type JWTConfig struct {
	Secret string `mapstructure:"secret"`
}

// LoadConfig 从文件或环境变量中加载配置
func LoadConfig() (*Config, error) {
	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath("./config")
	viper.AddConfigPath(".")
	viper.AutomaticEnv()

	if err := viper.ReadInConfig(); err != nil {
		return nil, err
	}

	var cfg Config
	if err := viper.Unmarshal(&cfg); err != nil {
		return nil, err
	}

	return &cfg, nil
}