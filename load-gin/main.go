package main

import (
	"fmt"
	"log"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
	"gorm.io/driver/postgres"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

var (
	static_dir  = GetEnvOrDefault("STATIC_DIR", "./")
	port        = GetEnvOrDefault("PORT", ":5444")
	db_host     = GetEnvOrDefault("DB_HOST", "localhost")
	db_port     = GetEnvOrDefault("DB_PORT", "5432")
	db_user     = GetEnvOrDefault("DB_URSER", "loader")
	db_name     = GetEnvOrDefault("DB_NAME", "load")
	db_password = GetEnvOrDefault("DB_PASSWORD", "load")
	db          *gorm.DB
)

func init() {
	dsn := fmt.Sprintf(
		"host=%v user=%v password=%v dbname=%v port=%v sslmode=disable",
		db_host, db_user, db_password, db_name, db_port,
	)

	var err error
	config := &gorm.Config{
		Logger: logger.Default.LogMode(logger.Silent),
	}
	db, err = gorm.Open(postgres.Open(dsn), config)
	if err != nil {
		log.Printf("connecting postgre db failed, and try to fall back to local sqlite db")
		db, err = gorm.Open(sqlite.Open("load.sqlite"), config)
		if err != nil {
			log.Fatalf("failed to connect db: %v", err)
		}
	}

	var sqlDB, _ = db.DB()
	sqlDB.SetMaxIdleConns(10)
	sqlDB.SetMaxOpenConns(20)
}

func main() {
	gin.SetMode(gin.ReleaseMode)

	r := gin.New()
	r.GET("/greeting", handleGreeting)
	r.GET("/inflation/:id", handleInflation)
	r.Static("/static", static_dir)

	log.Printf("start to serve on [%v]", port)
	log.Fatal(http.ListenAndServe(port, r))
}

func handleGreeting(c *gin.Context) {
	c.String(http.StatusOK, "Hello from Gin")
}

type Inflation struct {
	Id        int
	Region    string
	Year      string
	Inflation float32
	Unit      string
	Subregion string
	Country   string
}

func handleInflation(c *gin.Context) {
	id := c.Param("id")

	var inflation Inflation
	r := db.Table("inflation").First(&inflation, id)
	if r.Error != nil {
		c.String(http.StatusNotFound, "sql error: %v", r.Error)
	} else {
		c.JSON(http.StatusOK, inflation)
	}
}

func GetEnvOrDefault(key string, def string) string {
	var val = os.Getenv(key)
	if val == "" {
		val = def
	}
	return val
}
