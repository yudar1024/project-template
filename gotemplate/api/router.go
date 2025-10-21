package api

import (
	"github.com/gin-gonic/gin"
	"gotemplate/api/handlers"
	"gotemplate/internal/middleware"
)

// InitRouter 初始化并返回一个 Gin 引擎
func InitRouter() *gin.Engine {
	r := gin.New()
r.Use(gin.Recovery())
r.Use(middleware.RequestLogger())

	// 公共路由
	r.POST("/register", handlers.Register)
	r.POST("/login", handlers.Login)

	// 受保护的路由组
	authGroup := r.Group("/api")
	authGroup.Use(middleware.AuthMiddleware())
	{
		authGroup.GET("/profile", handlers.GetProfile)
	}

	return r
}