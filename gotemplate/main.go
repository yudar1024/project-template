package main

import (
	"gotemplate/api"
	"gotemplate/config"
	_ "gotemplate/docs" // swagger docs
	"gotemplate/pkg/database"
	"gotemplate/pkg/logger"

	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

// @title Go Template API
// @version 1.0
// @description This is a sample server for a Go template application.
// @termsOfService http://swagger.io/terms/

// @contact.name API Support
// @contact.url http://www.swagger.io/support
// @contact.email support@swagger.io

// @license.name Apache 2.0
// @license.url http://www.apache.org/licenses/LICENSE-2.0.html

// @host localhost:8080
// @BasePath /
func main() {
	// 加载配置
	cfg, err := config.LoadConfig()
	if err != nil {
		panic("Failed to load config: " + err.Error())
	}

	// 初始化日志
	logger.InitLogger(cfg.Log.Level, cfg.Log.Path)

	// 初始化数据库
	database.InitDB(cfg.Database.DSN)
	if database.DB == nil {
		logger.Warn("Database connection failed, running without database")
	}

	// 初始化 Gin 引擎
	r := api.InitRouter()

	// Swagger
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	// 启动服务
	if err := r.Run(cfg.Server.Address); err != nil {
		logger.Fatal("Failed to start server", "error", err)
	}
}