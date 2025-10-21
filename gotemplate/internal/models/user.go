package models

import (
	"time"
)

// User 定义了用户模型
type User struct {
	ID        uint      `gorm:"primarykey"`
	CreatedAt time.Time
	UpdatedAt time.Time
	DeletedAt *time.Time `gorm:"index"`
	Username  string    `gorm:"unique;not null"`
	Password  string    `gorm:"not null"`
}