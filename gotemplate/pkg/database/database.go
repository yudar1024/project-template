package database

import (
	"gorm.io/driver/mysql"
	"gorm.io/gorm"

	"gotemplate/pkg/logger"
)

var DB *gorm.DB

// InitDB 初始化数据库连接
func InitDB(dsn string) {
	var err error
	DB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{})
	if err != nil {
		logger.Error("Failed to connect to database", "error", err)
		DB = nil
		return
	}

	logger.Info("Database connection established")
}
