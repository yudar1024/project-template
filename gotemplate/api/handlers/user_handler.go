package handlers

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"gotemplate/internal/models"
	"gotemplate/pkg/database"
)

// @Summary Get user profile
// @Description Get the profile of the authenticated user
// @Tags user
// @Produce  json
// @Security ApiKeyAuth
// @Success 200 {object} models.User
// @Failure 401 {object} map[string]interface{}
// @Failure 404 {object} map[string]interface{}
// @Router /api/profile [get]
// GetProfile 处理获取用户个人资料的请求
func GetProfile(c *gin.Context) {
	userID, _ := c.Get("userID")

	var user models.User
	if err := database.DB.First(&user, userID).Error; err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "User not found"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"id":       user.ID,
		"username": user.Username,
	})
}