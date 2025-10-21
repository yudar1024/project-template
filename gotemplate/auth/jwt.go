package auth

import (
	"errors"
	"time"

	"github.com/dgrijalva/jwt-go"
	"gotemplate/config"
)

var jwtSecret []byte

// Claims 定义了 JWT 的载荷
type Claims struct {
	UserID uint `json:"user_id"`
	jwt.StandardClaims
}

// InitJWT 初始化 JWT
func InitJWT(cfg *config.Config) {
	jwtSecret = []byte(cfg.JWT.Secret)
}

// GenerateToken 生成 JWT
func GenerateToken(userID uint) (string, error) {
	claims := Claims{
		UserID: userID,
		StandardClaims: jwt.StandardClaims{
			ExpiresAt: time.Now().Add(24 * time.Hour).Unix(),
			Issuer:    "gotemplate",
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(jwtSecret)
}

// ParseToken 解析 JWT
func ParseToken(tokenString string) (*Claims, error) {
	token, err := jwt.ParseWithClaims(tokenString, &Claims{}, func(token *jwt.Token) (interface{}, error) {
		return jwtSecret, nil
	})

	if err != nil {
		return nil, err
	}

	if claims, ok := token.Claims.(*Claims); ok && token.Valid {
		return claims, nil
	} else {
		return nil, errors.New("invalid token")
	}
}