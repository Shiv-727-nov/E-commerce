@echo off
echo 🚀 Starting ShoeStop E-commerce Application...

REM Check if Docker is installed
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker is not installed. Please install Docker first.
    pause
    exit /b 1
)

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker Compose is not installed. Please install Docker Compose first.
    pause
    exit /b 1
)

echo 📦 Building and starting containers...
docker-compose up -d --build

echo ⏳ Waiting for services to start...
timeout /t 10 /nobreak >nul

echo ✅ Application is starting up!
echo.
echo 🌐 Access the application at:
echo    Frontend: http://localhost:3000
echo    Backend API: http://localhost:8080
echo.
echo 👤 Admin Login:
echo    Username: admin
echo    Password: admin123
echo.
echo 📊 To view logs:
echo    docker-compose logs -f
echo.
echo 🛑 To stop the application:
echo    docker-compose down
echo.
echo 🎉 Happy shopping!
pause
